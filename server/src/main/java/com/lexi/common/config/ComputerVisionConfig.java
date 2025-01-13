package com.lexi.common.config;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "azure.cognitive.computervision")

@NotNull
public class ComputerVisionConfig {

    private String endpoint;
    private String apiKey;

}
