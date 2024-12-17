package com.lexi.predict.service;

import com.lexi.predict.dto.UserResponseDto;
import org.springframework.stereotype.Service;

@Service
public class PredictService {

    public String calculateReadingLevel(UserResponseDto userResponseDto) {
        // Implement the scoring logic based on the user's responses and response times
        int score = 0;

        // Example scoring for self-reported dyslexia
        switch (userResponseDto.getDyslexiaAnswer()) {
            case "Yes":
                score += 2;
                break;
            case "I think I do":
                score += 1;
                break;
            case "No":
                score += 0;
                break;
            case "I prefer not to share":
                score += 0;  // Neutral impact
                break;
            default:
                score += 0;
        }

        // TODO: this is draft and will implement logic for questions later (Q2 to Q8)
        // will consider accuracy and response time here

        // Calculate total score and determine reading level
        String readingLevel = determineReadingLevel(score);

        return readingLevel;
    }

    private String determineReadingLevel(int score) {
        if (score >= 10) {
            return "Advanced";
        } else if (score >= 5) {
            return "Intermediate";
        } else if (score >= 0) {
            return "Basic";
        } else {
            return "Needs Support";
        }
    }
}
