package com.lexi.detection.service;

import com.lexi.detection.model.Category;
import com.lexi.detection.model.Question;
import com.lexi.detection.model.Option;
import com.lexi.detection.repository.CategoryRepository;
import com.lexi.detection.repository.OptionRepository;
import com.lexi.detection.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.google.gson.Gson;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Question> getQuestionsByCategory(String categoryName) {
        return questionRepository.findByCategory_Name(categoryName);
    }

    public void saveQuestionsFromJson(String filePath) {
        try {
            File file = ResourceUtils.getFile("classpath:" + filePath);
            Gson gson = new Gson();
            Map<String, Object> json = gson.fromJson(new FileReader(file), Map.class);

            Map<String, List<Map<String, Object>>> ageGroups = (Map<String, List<Map<String, Object>>>) json.get("age_groups");

            for (String ageGroup : ageGroups.keySet()) {
                List<Map<String, Object>> questions = ageGroups.get(ageGroup);

                // Save category
                Category category = categoryRepository.findByName(ageGroup);
                if (category == null) {
                    category = new Category();
                    category.setName(ageGroup);
                    category.setDescription("Questions for age group " + ageGroup);
                    category = categoryRepository.save(category);
                }

                // Process questions
                for (Map<String, Object> questionData : questions) {
                    String questionText = (String) questionData.get("prompt");

                    List<Question> existingQuestions = questionRepository.findByText(questionText);
                    if (!existingQuestions.isEmpty()) {
                        // skip or handle the logic if duplicate questions are found
                        continue;
                    }

                    Question question = new Question();
                    question.setCategory(category);
                    question.setText(questionText);
                    question.setType((String) questionData.get("type"));

                    question.setDisplay((String) questionData.get("display"));
                    question.setCorrectAnswer((String) questionData.get("correct_answer"));
                    question.setPoints(10); // Default points

                    Question savedQuestion = questionRepository.save(question);

                    List<String> options = (List<String>) questionData.get("options");
                    for (String optionText : options) {
                        // Check if the option already exists
                        Option existingOption = optionRepository.findByQuestionAndText(savedQuestion, optionText);
                        if (existingOption != null) {
                            continue; // Skip this option as it already exists
                        }

                        Option option = new Option();
                        option.setQuestion(savedQuestion);
                        option.setText(optionText);
                        optionRepository.save(option);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
