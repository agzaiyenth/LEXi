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
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private String text;

    private String imageUrl;

    private String type;

    private Integer correctAnswerIndex;

    private Integer points;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<Option> options;
}
