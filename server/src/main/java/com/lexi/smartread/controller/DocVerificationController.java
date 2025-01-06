package com.lexi.smartread.controller;

import com.lexi.smartread.service.DocVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/documents")
public class DocVerificationController {

    @Autowired
    private DocVerificationService verificationService;

    @GetMapping("/process")
    public String processDocument(@RequestParam String fileName) {
        if (verificationService.verifyDocument(fileName)) {
            return "Document is valid for processing.";
        }
        return "Document is not valid.";
    }
}

