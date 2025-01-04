package com.lexi.smartread.service;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class FileUploadService {

    @Value("${azure.storage.account-name}")  //add to application.properties
    private String accountName;

    @Value("${azure.storage.account-key}")  //add to application.properties
    private String accountKey;

    @Value("${azure.storage.container-name}")  //add to application.properties
    private String containerName;

    // Create a BlobServiceClient instance
    private BlobServiceClient getBlobServiceClient() {
        return new BlobServiceClientBuilder()
                .connectionString(
                        "DefaultEndpointsProtocol=https;AccountName="
                                + accountName + ";AccountKey="
                                + accountKey + ";EndpointSuffix=core.windows.net")
                .buildClient();
    }

    // Upload file to Azure Blob Storage


}