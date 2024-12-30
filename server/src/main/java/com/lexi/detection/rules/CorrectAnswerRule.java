package com.lexi.detection.rules;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Rule;
import org.jeasy.rules.api.Facts;

@Rule(name = "Correct Answer Rule", description = "Check if the user's answer is correct")
public class CorrectAnswerRule {

    @Condition
    public boolean when(Facts facts) {
        int selectedAnswerIndex = facts.get("selectedAnswerIndex");
        int correctAnswerIndex = facts.get("correctAnswerIndex");
        return selectedAnswerIndex == correctAnswerIndex;
    }

    @Action
    public void then(Facts facts) {
        int currentScore = facts.get("currentScore");
        int points = facts.get("points");
        facts.put("currentScore", currentScore + points);
    }
}

