package com.lexi.predict.controller;

import com.lexi.predict.dto.UserResponseDto;
import com.lexi.predict.service.PredictService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/predict")
@RequiredArgsConstructor
public class PredictController {

    private final PredictService predictService;

    @PostMapping("/level")
    public String predictReadingLevel(@RequestBody UserResponseDto userResponseDto) {
        return predictService.calculateReadingLevel(userResponseDto);
    }
}

