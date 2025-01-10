package com.lexi.common.config;

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
    private String voxbuddy_deployment;
    private String smartRead_deployment;
    private String apiVersion;

}
