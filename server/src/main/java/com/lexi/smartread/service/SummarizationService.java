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
    
}