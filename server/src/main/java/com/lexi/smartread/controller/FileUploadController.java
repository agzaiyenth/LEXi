package com.lexi.smartread.controller;

import com.lexi.smartread.service.ContentExtractService;
import com.lexi.smartread.service.DocVerificationService;
import com.lexi.smartread.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/documents")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;
    @Autowired
    private DocVerificationService verificationService;
    @Autowired
    private ContentExtractService extractionService;

    // Endpoint for document upload
    @PostMapping("/upload")
    public ResponseEntity<String> uploadDocument(@RequestParam("file") MultipartFile file) {
        // Check file type (only accept PDF for example)
        if (!file.getContentType().equals("application/pdf")) {
            return new ResponseEntity<>("Only PDF and DOC files are supported.", HttpStatus.BAD_REQUEST);
        }
        if (!file.getContentType().equals("application/docx")) {
            return new ResponseEntity<>("Only PDF and DOC files are supported.", HttpStatus.BAD_REQUEST);
        }

        // Check file size (limit to 50 MB)
        if (file.getSize() > 50 * 1024 * 1024) {
            return new ResponseEntity<>("File size exceeds the limit of 50MB.", HttpStatus.BAD_REQUEST);
        }

        try {
            // Call service class to upload the file
            String fileName = fileUploadService.uploadFile(file);
            return new ResponseEntity<>("File uploaded successfully: " + fileName, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("File upload failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/process")
    public String processDocument(@RequestParam String fileName) {
        try {
            // Step 1: Verify the document
            if (!verificationService.verifyDocument(fileName)) {
                return "Document verification failed.";
            }

            // Step 2: Extract content from the document
            String extractedContent = extractionService.extractContent(fileName);

            return "Extracted Content:\n" + extractedContent;
        } catch (Exception e) {
            return "Error during processing: " + e.getMessage();
        }
    }
}
