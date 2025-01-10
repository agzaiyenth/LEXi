package com.lexi.voxbuddy.controller;

import com.azure.ai.openai.realtime.RealtimeAsyncClient;
import com.azure.ai.openai.realtime.models.*;
import com.azure.ai.openai.realtime.utils.ConversationItem;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lexi.voxbuddy.dto.ControlMessage;
import com.lexi.voxbuddy.dto.TextDeltaMessage;
import com.lexi.voxbuddy.dto.TranscriptionMessage;
import com.lexi.voxbuddy.dto.UserMessage;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import reactor.core.Disposable;
import reactor.core.Disposables;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;

@Controller
@Slf4j
public class RealtimeAudioHandler extends TextWebSocketHandler {
    private final Map<String, SessionInfo> sessions = new java.util.concurrent.ConcurrentHashMap<>();

    @Getter
    private static class SessionInfo {
        private final WebSocketSession session;
        private final Disposable.Composite disposables;

        public SessionInfo(WebSocketSession session, Disposable.Composite disposables) {
            this.session = session;
            this.disposables = disposables;
        }

    }

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RealtimeAsyncClient realtimeAsyncClient;
    private String customInstruction;

    public RealtimeAudioHandler(RealtimeAsyncClient realtimeAsyncClient) {
        this.realtimeAsyncClient = realtimeAsyncClient;
    }

    @PostConstruct
    public void init() {
        log.atInfo().log("Starting RealtimeAsyncClient");

        // Load custom instruction from the JSON file
        String customInstruction = loadCustomInstruction();

        this.realtimeAsyncClient.start().block();

        realtimeAsyncClient.sendMessage(new SessionUpdateEvent(new RealtimeRequestSession()
                .setInputAudioFormat(RealtimeAudioFormat.PCM16)
                .setModalities(Arrays.asList(RealtimeRequestSessionModality.AUDIO, RealtimeRequestSessionModality.TEXT))
                .setInputAudioTranscription(new RealtimeAudioInputTranscriptionSettings()
                        .setModel(RealtimeAudioInputTranscriptionModel.WHISPER_1))
                .setTurnDetection(new RealtimeServerVadTurnDetection())
                .setVoice(RealtimeVoice.ALLOY)
                .setInstructions(customInstruction)) // Add instructions here
        ).block();

        log.atInfo().log("RealtimeAsyncClient started with custom instructions");
    }

    private String loadCustomInstruction() {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("voxbuddy-prompt.json")) {
            if (inputStream == null) {
                throw new IllegalStateException("voxbuddy-prompt.json not found in resources.");
            }
            JsonNode jsonNode = objectMapper.readTree(inputStream);
            return jsonNode.get("customInstruction").asText();
        } catch (IOException e) {
            log.atError().setCause(e).log("Failed to load custom instructions");
            throw new IllegalStateException("Unable to load custom instructions from voxbuddy-prompt.json", e);
        }
    }

    @PreDestroy
    public void destroy() {
        log.atInfo().log("Closing RealtimeAsyncClient");

        // 1) Stop your realtime client
        this.realtimeAsyncClient.stop().block();

        // 2) Dispose each session’s Composite
        sessions.values().forEach(info -> info.getDisposables().dispose());
        sessions.clear();

        log.atInfo().log("RealtimeAsyncClient closed and all session disposables are cleaned up");
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        log.atInfo().log("Connection established: " + session.getId());

        // Create a new composite for this session
        Disposable.Composite sessionDisposables = Disposables.composite();

        // Store session info
        SessionInfo sessionInfo = new SessionInfo(session, sessionDisposables);
        sessions.put(session.getId(), sessionInfo);

        // Send initial greeting
        ControlMessage connectionMessage = new ControlMessage("status")
                .setGreeting("connected");
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(connectionMessage)));

        log.info("Connection established with session: " + session.getId());
        // Start the event loop for *this* session
        startEventLoopForSession(session.getId());
    }



    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        log.atInfo().log("Connection closed: " + session.getId());
        log.atInfo().log("Close status: " + status);

        ControlMessage connectionMessage = new ControlMessage("status")
                .setGreeting("disconnected");
        if (session.isOpen()) {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(connectionMessage)));
        }

        log.info("Connection closed for session: " + session.getId());
        // Remove SessionInfo from the map
        SessionInfo info = sessions.remove(session.getId());
        if (info != null) {
            // Dispose of that session's subscriptions
            info.getDisposables().dispose();
        }
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Look up this session's info
        SessionInfo info = sessions.get(session.getId());
        if (info == null) {
            // Possibly the session is closed or not found
            return;
        }
        Disposable.Composite sessionDisposables = info.getDisposables();

        UserMessage userMessage = objectMapper.readValue(message.getPayload(), UserMessage.class);

        // Add to *this* session's disposables
        sessionDisposables.add(
                realtimeAsyncClient.sendMessage(ConversationItem.createUserMessage(userMessage.getText()))
                        .then(realtimeAsyncClient.sendMessage(new ResponseCreateEvent(
                                new RealtimeClientEventResponseCreateResponse())))
                        .subscribe(
                                it -> log.atInfo().log("User message sent"),
                                throwable -> handleSendError(throwable, session)
                        )
        );
    }


    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        log.atInfo().log("Binary message received");
        SessionInfo info = sessions.get(session.getId());
        if (info == null) return;

        Disposable.Composite sessionDisposables = info.getDisposables();

        sessionDisposables.add(
                realtimeAsyncClient.sendMessage(new InputAudioBufferAppendEvent(message.getPayload().array()))
                        .subscribe(
                                it -> log.atInfo().log("User audio sent"),
                                throwable -> handleSendError(throwable, session)
                        )
        );
    }


    private void handleSendError(Throwable throwable, WebSocketSession session) {
        log.atError().setCause(throwable).log("Error sending message");
        log.atInfo().log("Terminating connection");
        try {
            session.close(new CloseStatus(CloseStatus.SERVER_ERROR.getCode(), throwable.getMessage()));
            session.close();
        } catch (IOException e) {
            log.atError().setCause(e).log("Error closing session");
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.atError().setCause(exception).log("Transport error");
        super.handleTransportError(session, exception);
    }

    private void startEventLoopForSession(String sessionId) {
        SessionInfo sessionInfo = sessions.get(sessionId);
        if (sessionInfo == null) {
            // Session might have closed before we got here
            return;
        }
        Disposable.Composite disposables = sessionInfo.getDisposables();

        // We add all the subscriptions to this session's Disposable
        disposables.addAll(
                Arrays.asList(
                        getLooperFlux().ofType(ResponseAudioTranscriptDeltaEvent.class)
                                .subscribe(event -> handleTranscriptionDelta(sessionId, event)),
                        getLooperFlux().ofType(ResponseAudioTranscriptDoneEvent.class)
                                .subscribe(event -> handleTranscriptionDone(sessionId, event)),
                        getLooperFlux().ofType(ResponseAudioDeltaEvent.class)
                                .subscribe(event -> handleAudioDelta(sessionId, event)),
                        getLooperFlux().ofType(ResponseAudioDoneEvent.class)
                                .subscribe(event -> handleAudioDone(sessionId, event)),
                        getLooperFlux().ofType(ConversationItemInputAudioTranscriptionCompletedEvent.class)
                                .subscribe(event -> handleInputAudio(sessionId, event))
                )
        );
    }


    private void handleInputAudio(String sessionId, ConversationItemInputAudioTranscriptionCompletedEvent inputAudioEvent) {
        SessionInfo info = sessions.get(sessionId);
        if (info == null) return; // Session may have already closed

        WebSocketSession ws = info.getSession();
        try {
            // Send a "speech started" message
            String speechStartedPayload = objectMapper.writeValueAsString(new ControlMessage("speech_started"));
            ws.sendMessage(new TextMessage(speechStartedPayload));

            // Send the transcription message
            TranscriptionMessage transcription = new TranscriptionMessage(inputAudioEvent.getItemId())
                    .setText(inputAudioEvent.getTranscript());
            ws.sendMessage(new TextMessage(objectMapper.writeValueAsString(transcription)));

            log.atInfo().log("Input audio processed successfully. Transcript length: "
                    + inputAudioEvent.getTranscript().length());
        } catch (IOException e) {
            log.atError().setCause(e).log("Error sending input audio messages");
        }
    }


    private void handleAudioDone(String sessionId, ResponseAudioDoneEvent audioDoneEvent) {
        SessionInfo info = sessions.get(sessionId);
        if (info == null) return; // Session may have already closed

        log.atInfo().log("Audio done event received for session: " + sessionId);
        // You can send a message back to the client if needed
        // Example: Send a "completed" message (optional)
        try {
            WebSocketSession ws = info.getSession();
            String payload = objectMapper.writeValueAsString(new ControlMessage("audio_done"));
            ws.sendMessage(new TextMessage(payload));
            log.atInfo().log("Sent audio done message");
        } catch (IOException e) {
            log.atError().setCause(e).log("Error sending audio done message");
        }
    }


    private void handleAudioDelta(String sessionId, ResponseAudioDeltaEvent audioDeltaEvent) {
        SessionInfo info = sessions.get(sessionId);
        if (info == null) return; // Session may have already closed

        WebSocketSession ws = info.getSession();
        log.atInfo().log("New audio delta inbound for session: " + sessionId);

        try {
            ws.sendMessage(new BinaryMessage(audioDeltaEvent.getDelta()));
            log.atInfo().log("Sent audio delta message");
        } catch (IOException e) {
            log.atError().setCause(e).log("Error sending audio delta message");
        }
    }


    private void handleTranscriptionDone(String sessionId, ResponseAudioTranscriptDoneEvent transcriptDoneEvent) {
        SessionInfo info = sessions.get(sessionId);
        if (info == null) return; // Session may have already closed

        WebSocketSession ws = info.getSession();
        String contentId = transcriptDoneEvent.getItemId() + "-" + transcriptDoneEvent.getContentIndex();
        log.atInfo().log("Transcription done event received for contentId: " + contentId);

        try {
            String payload = objectMapper.writeValueAsString(new ControlMessage("done").setId(contentId));
            ws.sendMessage(new TextMessage(payload));
            log.atInfo().log("Sent transcription done message");
        } catch (IOException e) {
            log.atError().setCause(e).log("Error sending transcription done message");
        }
    }


    private void handleTranscriptionDelta(String sessionId, ResponseAudioTranscriptDeltaEvent textDelta) {
        SessionInfo info = sessions.get(sessionId);
        if (info == null) {
            return; // Session closed or not found
        }
        WebSocketSession ws = info.getSession();

        String contentId = textDelta.getItemId() + "-" + textDelta.getContentIndex();
        log.atInfo().log("New text delta inbound. Assigned contentId: " + contentId);

        TextDeltaMessage textDeltaMessage = new TextDeltaMessage(contentId, textDelta.getDelta());
        try {
            ws.sendMessage(new TextMessage(objectMapper.writeValueAsString(textDeltaMessage)));
            log.atInfo().log("Sent text delta message immediately");
        } catch (Exception e) {
            log.atError().setCause(e).log("Error sending text delta message");
        }
    }



    private Flux<RealtimeServerEvent> getLooperFlux() {
        return realtimeAsyncClient.getServerEvents().onErrorResume((throwable) -> {
            // Log the error and continue listening for events.
            if (throwable instanceof ServerErrorReceivedException error) {
                RealtimeServerEventErrorError errorDetails = error.getErrorDetails();
                log.atError().setCause(throwable)
                        .addKeyValue("eventId", errorDetails.getEventId())
                        .addKeyValue("code", String.valueOf(errorDetails.getCode()))
                        .addKeyValue("message", errorDetails.getMessage())
                        .addKeyValue("type", errorDetails.getType())
                        .addKeyValue("param", errorDetails.getParam())
                        .log("Received a ServerErrorReceivedException");
                log.atError().log("Error message: " + errorDetails.getMessage());
            } else {
                log.atError().setCause(throwable).log("Error sent from the Realtime server");
            }
            // errors where eventId is defined are not a terminal error.
            // otherwise, we could restart with a new session, if the configuration is always the same.
            return realtimeAsyncClient.getServerEvents();
        });
    }
}