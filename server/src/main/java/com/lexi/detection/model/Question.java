package com.lexi.detection.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category; // Example: "kids", "teens", "adults"

    private String type; // Question type: "multiple-choice", "text", etc.

    private String text; // The actual question text

    private String imageUrl; // URL for image-based questions

    @ElementCollection
    private List<String> options; // Multiple-choice options

    private Integer correctAnswerIndex; // Index of the correct answer (for MCQs)

    private Integer points; // Points for the question
}
