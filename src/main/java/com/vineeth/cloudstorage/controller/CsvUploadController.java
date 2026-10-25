package com.vineeth.cloudstorage.controller;

import com.vineeth.cloudstorage.service.GcsStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class CsvUploadController {

    private final GcsStorageService gcsStorageService;

    public CsvUploadController(GcsStorageService gcsStorageService) {
        this.gcsStorageService = gcsStorageService;
    }

    @PostMapping(value = "/upload", produces = "application/json")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {

        String location = gcsStorageService.uploadCsv(file);

        return ResponseEntity.ok(Map.of(
                "message", "CSV uploaded successfully",
                "location", location
        ));
    }
}
