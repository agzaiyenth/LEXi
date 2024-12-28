package com.lexi.voxbuddy.controller;

import com.azure.ai.openai.realtime.RealtimeAsyncClient;
import com.azure.ai.openai.realtime.models.ConversationItemInputAudioTranscriptionCompletedEvent;
import com.azure.ai.openai.realtime.models.InputAudioBufferAppendEvent;
import com.azure.ai.openai.realtime.models.RealtimeAudioFormat;
import com.azure.ai.openai.realtime.models.RealtimeAudioInputTranscriptionModel;
import com.azure.ai.openai.realtime.models.RealtimeAudioInputTranscriptionSettings;
import com.azure.ai.openai.realtime.models.RealtimeClientEventResponseCreateResponse;
import com.azure.ai.openai.realtime.models.RealtimeRequestSession;
import com.azure.ai.openai.realtime.models.RealtimeRequestSessionModality;
import com.azure.ai.openai.realtime.models.RealtimeServerEvent;
import com.azure.ai.openai.realtime.models.RealtimeServerEventErrorError;
import com.azure.ai.openai.realtime.models.RealtimeServerVadTurnDetection;
import com.azure.ai.openai.realtime.models.RealtimeVoice;
import com.azure.ai.openai.realtime.models.ResponseAudioDeltaEvent;
import com.azure.ai.openai.realtime.models.ResponseAudioDoneEvent;
import com.azure.ai.openai.realtime.models.ResponseAudioTranscriptDeltaEvent;
import com.azure.ai.openai.realtime.models.ResponseAudioTranscriptDoneEvent;
import com.azure.ai.openai.realtime.models.ResponseCreateEvent;
import com.azure.ai.openai.realtime.models.ServerErrorReceivedException;
import com.azure.ai.openai.realtime.models.SessionUpdateEvent;
import com.azure.ai.openai.realtime.utils.ConversationItem;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lexi.voxbuddy.dto.ControlMessage;
import com.lexi.voxbuddy.dto.TextDeltaMessage;
import com.lexi.voxbuddy.dto.TranscriptionMessage;
import com.lexi.voxbuddy.dto.UserMessage;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
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
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Arrays;
import java.util.Map;

@Controller
@Slf4j
public class RealtimeAudioHandler extends TextWebSocketHandler {

     private final ObjectMapper objectMapper = new ObjectMapper();
    private final Disposable.Composite disposables = Disposables.composite();
    private WebSocketSession currentSession = null;
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
        this.realtimeAsyncClient.stop().block();
        disposables.dispose();
        log.atInfo().log("RealtimeAsyncClient closed");
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.atInfo().log("Connection established: " + session.getId());
        this.currentSession = session;

        ControlMessage controlMessage = new ControlMessage("connected")
                .setGreeting("You are now connected to the Spring Boot server");
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(controlMessage)));

        startEventLoop();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        log.atInfo().log("Connection closed: " + session.getId());
        log.atInfo().log("Close status: " + status);
        disposables.dispose();
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("Message received");
        UserMessage userMessage = objectMapper.readValue(message.getPayload(), UserMessage.class);
        disposables.add(realtimeAsyncClient.sendMessage(ConversationItem.createUserMessage(userMessage.getText()))
                .then(realtimeAsyncClient.sendMessage(new ResponseCreateEvent(
                        new RealtimeClientEventResponseCreateResponse())))
                .subscribe(it -> log.atInfo().log("User message sent"),
                        throwable -> handleSendError(throwable, session)));
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        log.atInfo().log("Binary message received");
        disposables.add(realtimeAsyncClient.sendMessage(
                        new InputAudioBufferAppendEvent(message.getPayload().array()))
                .subscribe(it -> log.atInfo().log("User audio sent"),
                        throwable -> handleSendError(throwable, session)));
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

    private void startEventLoop() {
        disposables.addAll(Arrays.asList(
                getLooperFlux().ofType(ResponseAudioTranscriptDeltaEvent.class)
                        .subscribe(this::handleTranscriptionDelta),
                getLooperFlux().ofType(ResponseAudioTranscriptDoneEvent.class)
                        .subscribe(this::handleTranscriptionDone),
                getLooperFlux().ofType(ResponseAudioDeltaEvent.class)
                        .subscribe(this::handleAudioDelta),
                getLooperFlux().ofType(ResponseAudioDoneEvent.class)
                        .subscribe(this::handleAudioDone),
                getLooperFlux().ofType(ConversationItemInputAudioTranscriptionCompletedEvent.class)
                        .subscribe(this::handleInputAudio)
        ));
    }

    private void handleInputAudio(ConversationItemInputAudioTranscriptionCompletedEvent inputAudioEvent) {
        try {
            String payload = objectMapper.writeValueAsString(new ControlMessage("speech_started"));
            currentSession.sendMessage(new TextMessage(payload));
            TranscriptionMessage transcription = new TranscriptionMessage(inputAudioEvent.getItemId())
                    .setText(inputAudioEvent.getTranscript());
            currentSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(transcription)));
            log.atInfo().log("Input audio successfully processed of length: " + inputAudioEvent.getTranscript().length());
        } catch (IOException e) {
            log.atError().setCause(e).log("Error sending speech started message");
        }
    }

    private void handleAudioDone(ResponseAudioDoneEvent audioDoneEvent) {
        log.atInfo().log("Audio done event received");
        // no-op
    }

    private void handleAudioDelta(ResponseAudioDeltaEvent audioDeltaEvent) {
        log.atInfo().log("New audio delta inbound");
        try {
            currentSession.sendMessage(new BinaryMessage(audioDeltaEvent.getDelta()));
        } catch (IOException e) {
            log.atError().setCause(e).log("Error sending audio delta message");
        }
    }

    private void handleTranscriptionDone(ResponseAudioTranscriptDoneEvent transcriptDoneEvent) {
        String contentId = transcriptDoneEvent.getItemId() + "-" + transcriptDoneEvent.getContentIndex();
        log.atInfo().log("Transcription done event received for contentId: " + contentId);
        try {
            String payload = objectMapper.writeValueAsString(new ControlMessage("done")
                    .setId(contentId));
            this.currentSession.sendMessage(new TextMessage(payload));
        } catch (Exception e) {
            log.atError().setCause(e).log("Error sending done message");
        }
    }

    private void handleTranscriptionDelta(ResponseAudioTranscriptDeltaEvent textDelta) {
        String contentId = textDelta.getItemId() + "-" + textDelta.getContentIndex();
        log.atInfo().log("New text delta inbound. Assigned contentId: " + contentId);
        TextDeltaMessage textDeltaMessage = new TextDeltaMessage(contentId, textDelta.getDelta());
        Mono.delay(Duration.ofMillis(300)) // Add debounce time (e.g., 300ms)
                .then(Mono.fromRunnable(() -> {
                    try {
                        this.currentSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(textDeltaMessage)));
                        log.atInfo().log("Sent text delta message after debounce");
                    } catch (Exception e) {
                        log.atError().setCause(e).log("Error sending text delta message");
                    }
                }))
                .subscribe();
    }

    private Flux<RealtimeServerEvent> getLooperFlux() {
        return realtimeAsyncClient.getServerEvents().onErrorResume((throwable) -> {
            // Log the error and continue listening for events.
            if (throwable instanceof ServerErrorReceivedException) {
                ServerErrorReceivedException error = (ServerErrorReceivedException) throwable;
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