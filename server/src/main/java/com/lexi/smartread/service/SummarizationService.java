package com.lexi.smartread.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;


@Service
@Slf4j
public class SummarizationService {
    public String summarizeContent(String extractedContent) {
        try {
            //Load the summarization prompt template
            String prompt = loadCustomInstruction();

            String formattedPrompt = prompt.replace("{content}", extractedContent);

            // Send the prompt to the deployed model for summarization
            return callSummarizationModel(formattedPrompt);
        } catch (Exception e) {
            throw new RuntimeException("Error during summarization: " + e.getMessage());
        }
    }

    private String loadCustomInstruction() {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("smartread-summarizePrompt.json")) {
            if (inputStream == null) {
                throw new IllegalStateException("smartread-summarizePrompt.json not found in resources.");
            }
            JsonNode jsonNode = objectMapper.readTree(inputStream);
            return jsonNode.get("customInstruction").asText();
        } catch (IOException e) {
            log.atError().setCause(e).log("Failed to load custom instructions");
            throw new IllegalStateException("Unable to load custom instructions from smartread-summarizePrompt.json", e);
        }
    }

    private String callSummarizationModel(String prompt) {
        // Simulate a call to the model
        // Replace this with the actual HTTP client implementation
        return "Summarized content based on the prompt: " + prompt;
    }

}