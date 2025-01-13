package com.lexi.detection;

import com.lexi.detection.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class QuestionDataLoader implements CommandLineRunner {

    @Autowired
    private QuestionService questionService;

    @Override
    public void run(String... args) throws Exception {
        String filePath = "data/questions.json";
        questionService.saveQuestionsFromJson(filePath);
        System.out.println("Questions loaded successfully!");
    }

}
