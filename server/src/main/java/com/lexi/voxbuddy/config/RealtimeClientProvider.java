package com.lexi.voxbuddy.config;

import com.azure.ai.openai.realtime.RealtimeAsyncClient;
import com.azure.ai.openai.realtime.RealtimeClient;
import com.azure.ai.openai.realtime.RealtimeClientBuilder;
import com.azure.core.credential.KeyCredential;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RealtimeClientProvider {

    private final AzureOpenAIConfiguration azureOpenAIConfiguration;

    public RealtimeClientProvider(AzureOpenAIConfiguration azureOpenAIConfiguration) {
        this.azureOpenAIConfiguration = azureOpenAIConfiguration;
    }

    @Bean
    public RealtimeClient buildClient() {
        return getRealtimeClientBuilder().buildClient();
    }

    @Bean
    public RealtimeAsyncClient buildAsyncClient() {
        return getRealtimeClientBuilder().buildAsyncClient();
    }

    private RealtimeClientBuilder getRealtimeClientBuilder() {

        return new RealtimeClientBuilder()
                .endpoint(azureOpenAIConfiguration.getEndpoint())
                .credential(new KeyCredential(azureOpenAIConfiguration.getApiKey()))
                .deploymentOrModelName(azureOpenAIConfiguration.getDeployment());
    }

}
