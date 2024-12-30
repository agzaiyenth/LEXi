package com.lexi.voxbuddy.config;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "azureopenai.realtime")
@NotNull
public class AzureOpenAIConfiguration {

    private String endpoint;
    private String apiKey;
    private String deployment;
    private String apiVersion;

}
