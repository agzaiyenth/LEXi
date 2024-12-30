package com.lexi.detection.repository;

import com.lexi.detection.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByCategoryId(Long categoryId);
    List<Question> findByCategoryName(String categoryName);
}
