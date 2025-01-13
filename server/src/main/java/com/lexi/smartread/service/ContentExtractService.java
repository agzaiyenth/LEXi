package com.lexi.smartread.service;

import com.azure.ai.formrecognizer.FormRecognizerClient;
import com.azure.ai.formrecognizer.FormRecognizerClientBuilder;
import com.azure.ai.formrecognizer.models.FormPage;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.specialized.BlockBlobClient;
import com.lexi.common.config.BlobConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.lexi.common.config.ComputerVisionConfig;

import java.util.List;

@Service
public class ContentExtractService {

    @Value("${azure.storage.account-name}")  //add to application.properties
    private String accountName;

    @Value("${azure.storage.container-name}")
    private String containerName;

    private final ComputerVisionConfig computerVisionConfig;
    private final BlobConfig blobConfig;

    public ContentExtractService(ComputerVisionConfig computerVisionConfig, BlobConfig blobConfig) {
        this.computerVisionConfig = computerVisionConfig;
        this.blobConfig = blobConfig;
    }
    public String extractContent(String fileName) {
        //Retrieve the document URL from Blob Storage
        String fileUrl = getBlobFileUrl(fileName);

        // Perform OCR using Azure Cognitive Services
        return performOCR(fileUrl);

        //add summarization service




    }

    private String getBlobFileUrl(String fileName) {
        String apiKey = blobConfig.getApiKey();

        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(
                        "DefaultEndpointsProtocol=https;AccountName="
                                + accountName + ";AccountKey="
                                + apiKey + ";EndpointSuffix=core.windows.net")
                .buildClient();


        BlockBlobClient blobClient = blobServiceClient
                .getBlobContainerClient(containerName)
                .getBlobClient(fileName)
                .getBlockBlobClient();

        return blobClient.getBlobUrl(); // Returns the URL of the uploaded file
    }

    private String performOCR(String fileUrl) {

        String visionEndpoint = computerVisionConfig.getEndpoint();
        String visionKey = computerVisionConfig.getApiKey();
        // Initialize the Form Recognizer Client
        FormRecognizerClient client = new FormRecognizerClientBuilder()
                .endpoint(visionEndpoint)
                .credential(new AzureKeyCredential(visionKey))
                .buildClient();

        // Use the client to extract text from the document
        StringBuilder extractedText = new StringBuilder();

        List<FormPage> pages = client.beginRecognizeContentFromUrl(fileUrl)
                .getFinalResult();

        for (com.azure.ai.formrecognizer.models.FormPage page : pages) {
            page.getLines().forEach(line ->
                    extractedText.append(line.getText()).append("\n")
            );
        }

        return extractedText.toString(); // Returns extracted text
    }



}