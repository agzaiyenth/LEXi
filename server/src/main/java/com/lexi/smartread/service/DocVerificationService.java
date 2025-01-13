package com.lexi.smartread.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobProperties;
import org.springframework.stereotype.Service;

@Service
public class DocVerificationService {
    private final String connectionString = "DefaultEndpointsProtocol=https;AccountName=lexistorageblob;AccountKey=Aqc8AbEuU6qgylA8WJNqef1mKKimrb8sQuoTYQygB5BckvkHdplcWL9ne/4fgGDxwnROkDtdjiby+AStjen09w==;EndpointSuffix=core.windows.net";
    private final String containerName = "lexifilecontainer";

    public boolean verifyDocument(String fileName) {
        // Initialize BlobServiceClient
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();

        BlobClient blobClient = blobServiceClient.getBlobContainerClient(containerName).getBlobClient(fileName);

        // Check if the file exists
        if (!blobClient.exists()) {
            throw new RuntimeException("File not found in storage: " + fileName);
        }

        // Retrieve file properties
        BlobProperties properties = blobClient.getProperties();

        // Validate file size
        if (properties.getBlobSize() == 0) {
            throw new RuntimeException("File is empty: " + fileName);
        }
        // Validate file type
        String contentType = properties.getContentType();
        if (!contentType.equals("application/pdf") && !contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
            throw new RuntimeException("Unsupported file type: " + contentType);
        }

        return true; // File is valid
    }

    }
