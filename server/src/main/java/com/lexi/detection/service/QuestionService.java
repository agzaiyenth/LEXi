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
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

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

}
