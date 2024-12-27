package com.lexi.voxbuddy.controller;

import com.azure.ai.openai.realtime.RealtimeAsyncClient;
import com.azure.ai.openai.realtime.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import reactor.core.Disposable;
import reactor.core.Disposables;

import java.util.Arrays;

@Controller
@Slf4j
@Scope("prototype")
public class RealtimeAudioHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(RealtimeAudioHandler.class);
    private final RealtimeAsyncClient realtimeAsyncClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Disposable.Composite disposables = Disposables.composite();

    public RealtimeAudioHandler(RealtimeAsyncClient realtimeAsyncClient) {
        this.realtimeAsyncClient = realtimeAsyncClient;
    }

    @PostConstruct
    public void init() {
        log.info("Starting RealtimeAsyncClient");
        realtimeAsyncClient.start().block();
        realtimeAsyncClient.sendMessage(new SessionUpdateEvent(new RealtimeRequestSession()
                .setInputAudioFormat(RealtimeAudioFormat.PCM16)
                .setModalities(Arrays.asList(RealtimeRequestSessionModality.AUDIO, RealtimeRequestSessionModality.TEXT))
                .setInputAudioTranscription(new RealtimeAudioInputTranscriptionSettings()
                        .setModel(RealtimeAudioInputTranscriptionModel.WHISPER_1))
                .setTurnDetection(new RealtimeServerVadTurnDetection())
                .setVoice(RealtimeVoice.ALLOY)
        )).block();
        log.info("RealtimeAsyncClient started successfully");
    }


    @PreDestroy
    public void destroy() {
        logger.info("Stopping RealtimeAsyncClient...");
        realtimeAsyncClient.stop().block();
        disposables.dispose();
        logger.info("RealtimeAsyncClient stopped.");
    }

    @Override
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        logger.info("Binary message received.");
        disposables.add(realtimeAsyncClient.sendMessage(new InputAudioBufferAppendEvent(message.getPayload().array()))
                .subscribe(
                        success -> logger.info("Audio buffer appended."),
                        error -> handleSendError(error, session)));
    }

    private void handleSendError(Throwable throwable, WebSocketSession session) {
        logger.error("Error sending message", throwable);
        try {
            session.close();
        } catch (Exception e) {
            logger.error("Error closing WebSocket session", e);
        }
    }
}
