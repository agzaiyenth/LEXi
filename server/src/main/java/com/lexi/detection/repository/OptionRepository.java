package com.lexi.detection.repository;

import com.lexi.detection.model.Option;
import com.lexi.detection.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {
    List<Option> findByQuestionId(Long questionId);
    Option findByQuestionAndText(Question question, String text);

}
