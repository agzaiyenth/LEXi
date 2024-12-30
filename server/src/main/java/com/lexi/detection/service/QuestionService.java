package com.lexi.detection.service;

import com.lexi.detection.model.Question;
import com.lexi.detection.rules.CorrectAnswerRule;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {


    public List<Question> getQuestionsByCategory(String category) {

        return List.of(
                new Question(1L, "kids", "multiple-choice", "What is the spelling of this?", "cat.png",
                        List.of("cat", "dat", "bat"), 0, 3)
        );
    }


    public int evaluateAnswers(List<Integer> selectedAnswers, List<Question> questions) {
        int totalScore = 0;

        Rules rules = new Rules();
        rules.register(new CorrectAnswerRule());

        DefaultRulesEngine rulesEngine = new DefaultRulesEngine();

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);

            Facts facts = new Facts();
            facts.put("selectedAnswerIndex", selectedAnswers.get(i));
            facts.put("correctAnswerIndex", question.getCorrectAnswerIndex());
            facts.put("currentScore", totalScore);
            facts.put("points", question.getPoints());

            rulesEngine.fire(rules, facts);

            totalScore = facts.get("currentScore");
        }

        return totalScore;
    }
}

