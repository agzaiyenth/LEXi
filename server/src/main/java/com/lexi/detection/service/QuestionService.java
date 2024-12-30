package com.lexi.detection.service;

import com.lexi.detection.model.Category;
import com.lexi.detection.model.Question;
import com.lexi.detection.model.Option;
import com.lexi.detection.repository.CategoryRepository;
import com.lexi.detection.repository.OptionRepository;
import com.lexi.detection.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public Question saveQuestion(String categoryName, String text, String type, List<String> options, int correctAnswerIndex, int points) {

        Category category = categoryRepository.findByName(categoryName);
        if (category == null) {
            throw new RuntimeException("Category not found: " + categoryName);
        }

        Question question = new Question();
        question.setCategory(category);
        question.setText(text);
        question.setType(type);
        question.setCorrectAnswerIndex(correctAnswerIndex);
        question.setPoints(points);

        // Save the question
        Question savedQuestion = questionRepository.save(question);

        // Save the options
        for (String optionText : options) {
            Option option = new Option();
            option.setQuestion(savedQuestion);
            option.setText(optionText);
            optionRepository.save(option);
        }

        return savedQuestion;
    }
}
