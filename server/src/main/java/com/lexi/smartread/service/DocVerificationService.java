package com.lexi.smartread.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobProperties;
import com.lexi.common.config.BlobConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DocVerificationService {

    @Value("${azure.storage.account-name}")  //add to application.properties
    private String accountName;

    @Value("${azure.storage.container-name}")  //add to application.properties
    private String containerName;

    private final BlobConfig blobConfig;

    public DocVerificationService(BlobConfig blobConfig) {
        this.blobConfig = blobConfig;
    }

    public boolean verifyDocument(String fileName) {

        // Initialize BlobServiceClient
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString("DefaultEndpointsProtocol=https;" +
                        "AccountName="  + accountName +
                        "AccountKey=" +blobConfig.getApiKey()
                        + ";EndpointSuffix=core.windows.net" )
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

        return false; // File is valid
    }

    }
