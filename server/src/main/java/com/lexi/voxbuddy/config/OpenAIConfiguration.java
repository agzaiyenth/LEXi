package com.lexi.voxbuddy.config;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@ConfigurationProperties(prefix = "openai.realtime")
@ConditionalOnProperty(
        name = "server.backend",
        havingValue = "openai"
)
class OpenAIConfiguration {
    @Value("${openai.realtime.apiKey}")
    private String openaiKey;

    @Value("${openai.realtime.model}")
    private String openAIModel;

}