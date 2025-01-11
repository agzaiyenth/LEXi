package com.lexi.smartread.service;

import com.azure.ai.formrecognizer.FormRecognizerClient;
import com.azure.ai.formrecognizer.FormRecognizerClientBuilder;
import com.azure.ai.formrecognizer.models.FormPage;
import com.azure.ai.formrecognizer.models.RecognizedForm;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.specialized.BlockBlobClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContentExtractService {

    // Azure Blob Storage connection string, container name, and Azure computer vision credentials
    private final String blobConnectionString = "<DefaultEndpointsProtocol=https;AccountName=lexistorageblob;AccountKey=Aqc8AbEuU6qgylA8WJNqef1mKKimrb8sQuoTYQygB5BckvkHdplcWL9ne/4fgGDxwnROkDtdjiby+AStjen09w==;EndpointSuffix=core.windows.net>";
    private final String containerName = "<lexifilecontainer>";
    private final String visionEndpoint = "<https://lexicomputervision.cognitiveservices.azure.com/>"; //azure vision endpoint
    private final String visionKey = "<BNnu3HEhkyhxwOYA2M062Q05LEPXUzMyR28tzoRqNoh3A7jzgFevJQQJ99BAACYeBjFXJ3w3AAAFACOGc36i>"; // azure vision key

    public String extractContent(String fileName) {
        // Step 1: Retrieve the document URL from Blob Storage
        String fileUrl = getBlobFileUrl(fileName);

        // Step 2: Perform OCR using Azure Cognitive Services
        return performOCR(fileUrl);
    }

    private String getBlobFileUrl(String fileName) {
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(blobConnectionString)
                .buildClient();

        BlockBlobClient blobClient = blobServiceClient
                .getBlobContainerClient(containerName)
                .getBlobClient(fileName)
                .getBlockBlobClient();

        return blobClient.getBlobUrl(); // Returns the URL of the uploaded file
    }

    private String performOCR(String fileUrl) {
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