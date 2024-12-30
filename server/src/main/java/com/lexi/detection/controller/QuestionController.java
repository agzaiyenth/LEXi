package com.lexi.detection.controller;

import com.lexi.detection.model.Question;
import com.lexi.detection.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/{category}")
    public List<Question> getQuestionsByCategory(@PathVariable String category) {
        return questionService.getQuestionsByCategory(category);
    }

    @PostMapping
    public Question saveQuestion(@RequestBody Question question) {
        return questionService.saveQuestion(question);
    }
}
