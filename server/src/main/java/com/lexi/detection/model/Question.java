package com.lexi.detection.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "text"))
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private String text; // Question text
    private String type; // Question type (mcq or image_mcq)

    private String imagePath; // Path to images

    private String display; // Additional display text or hints
    private String correctAnswer; // Correct answer text
    private Integer correctAnswerIndex; // Correct answer by index
    private Integer points;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<Option> options;

}
