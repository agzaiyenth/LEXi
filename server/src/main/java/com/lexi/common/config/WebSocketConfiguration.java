package com.lexi.common.config;

import com.azure.ai.openai.realtime.RealtimeAsyncClient;
import com.lexi.voxbuddy.controller.RealtimeAudioHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    @Autowired
    private RealtimeAsyncClient realtimeAsyncClient;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new RealtimeAudioHandler(realtimeAsyncClient),
                "/realtime").setAllowedOrigins("*");
    }
}