package com.lexi.predict.dto;

import lombok.Data;

@Data
public class UserResponseDto {
    private String dyslexiaAnswer;
    private boolean[] questionAnswers;  // Array to hold answers for questions 2-8
    private long[] responseTimes;       // Array to hold response times for questions 2-8
}
