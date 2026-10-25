package com.vineeth.cloudstorage.service;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class GcsStorageService {

    private final Storage storage;
    private final String bucketName;

    public  GcsStorageService(@Value("${gcs.bucket-name}") String bucketName) {
        this.storage = StorageOptions.getDefaultInstance().getService();
        this.bucketName = bucketName;
    }

    public String uploadCsv(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("CSV file is empty");
        }

        String filename = file.getOriginalFilename();

        if (filename == null ||
                !filename.toLowerCase().endsWith(".csv")) {
            throw new IllegalArgumentException(
                    "Only CSV files are allowed");
        }

        String objectName =
                "uploads/" + UUID.randomUUID() + "-" + filename;

        BlobInfo blobInfo = BlobInfo.newBuilder(
                        bucketName, objectName)
                .setContentType("text/csv")
                .build();

        storage.create(blobInfo, file.getBytes());

        return "gs://" + bucketName + "/" + objectName;
    }
}
