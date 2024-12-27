package com.lexi.voxbuddy.config;

import com.azure.ai.openai.realtime.RealtimeAsyncClient;
import com.azure.ai.openai.realtime.RealtimeClient;
import com.azure.ai.openai.realtime.RealtimeClientBuilder;
import com.azure.core.credential.KeyCredential;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class RealtimeClientProvider {

    private final AzureOpenAIConfiguration azureOpenAIConfiguration;

    @Autowired
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
