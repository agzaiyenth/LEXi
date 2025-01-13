package com.lexi.smartread.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.lexi.common.config.BlobConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class FileUploadService {

    @Value("${azure.storage.account-name}")  //add to application.properties
    private String accountName;

    @Value("${azure.storage.container-name}")  //add to application.properties
    private String containerName;

    private final BlobConfig blobConfig;

    public FileUploadService(BlobConfig blobConfig){
        this.blobConfig = blobConfig;
    }

    // Create a BlobServiceClient instance
    private BlobServiceClient getBlobServiceClient() {
        String apiKey = blobConfig.getApiKey();

        return new BlobServiceClientBuilder()
                .connectionString(
                        "DefaultEndpointsProtocol=https;AccountName="
                                + accountName + ";AccountKey="
                                + apiKey + ";EndpointSuffix=core.windows.net")
                .buildClient();
    }

    // Upload file to Azure Blob Storage
    public String uploadFile(MultipartFile file) throws IOException {
        // Generate a unique name for the file
        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();

        // Get a reference to the container
        BlobServiceClient blobServiceClient = getBlobServiceClient();
        BlobClient blobClient = blobServiceClient.getBlobContainerClient(containerName)
                .getBlobClient(fileName);

        // Upload the file to the container
        try (InputStream inputStream = file.getInputStream()) {
            blobClient.upload(inputStream, file.getSize(), true);  // Overwrite if file exists
        }

        return fileName; // Return the blob name (file identifier)
    }

}