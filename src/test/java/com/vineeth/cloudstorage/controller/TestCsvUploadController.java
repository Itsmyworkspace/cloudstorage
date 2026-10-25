package com.vineeth.cloudstorage.controller;

import com.vineeth.cloudstorage.service.GcsStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CsvUploadController.class)
public class TestCsvUploadController {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GcsStorageService gcsStorageService;

    @Test
    public void uploadCsv_shouldReturnSuccess_whenValidFileUploaded()
            throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "employees.csv",
                "text/csv",
                "id,name\n1,John\n2,Alice".getBytes()
        );

        when(gcsStorageService.uploadCsv(any()))
                .thenReturn("gs://my-bucket/employees.csv");

        mockMvc.perform(multipart("/api/files/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message")
                        .value("CSV uploaded successfully"))
                .andExpect(jsonPath("$.location")
                        .value("gs://my-bucket/employees.csv"));

        verify(gcsStorageService, times(1))
                .uploadCsv(any());
    }

    @Test
    public void uploadCsv_shouldReturnError_whenFileMissing()
            throws Exception {
        mockMvc.perform(multipart("/api/files/upload"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(gcsStorageService);
    }

    @Test
    void uploadCsv_shouldPropagateIOException_whenStorageFails()
            throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "employees.csv",
                "text/csv",
                "id,name\n1,John".getBytes()
        );

        when(gcsStorageService.uploadCsv(any()))
                .thenThrow(new IOException("GCS upload failed"));

        mockMvc.perform(multipart("/api/files/upload")
                        .file(file))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message")
                        .value("Failed to upload CSV"));

        verify(gcsStorageService, times(1))
                .uploadCsv(any());
    }
}
