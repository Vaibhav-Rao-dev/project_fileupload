package com.enterprise.storage.presentation.api;

import com.enterprise.storage.domain.model.FileMetadataContext;
import com.enterprise.storage.domain.model.UploadStatus;
import com.enterprise.storage.domain.ports.api.FileStorageUseCase;
import com.enterprise.storage.presentation.api.dto.InitiateUploadRequest;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileStorageResourceTest {

    @Mock
    private FileStorageUseCase fileStorageUseCase;

    @InjectMocks
    private FileStorageResource resource;

    @Test
    void initiateUpload_ShouldReturn201Created() {
        // Given
        InitiateUploadRequest request = new InitiateUploadRequest("video.mp4", "video/mp4", 1024L);
        FileMetadataContext mockContext = new FileMetadataContext(UUID.randomUUID(), "video.mp4", "video/mp4", 1024L, UploadStatus.INITIATED, Instant.now());
        when(fileStorageUseCase.initiateUpload(anyString(), anyString(), anyLong())).thenReturn(mockContext);

        // When
        Response response = resource.initiateUpload(request);

        // Then
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(mockContext, response.getEntity());
        verify(fileStorageUseCase, times(1)).initiateUpload("video.mp4", "video/mp4", 1024L);
    }

    @Test
    void uploadChunk_ShouldReturn202Accepted() {
        // Given
        UUID fileId = UUID.randomUUID();
        InputStream chunkStream = new ByteArrayInputStream("chunk-data".getBytes());
        long chunkSize = 10L;

        // When
        Response response = resource.uploadChunk(fileId, chunkStream, chunkSize);

        // Then
        assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus());
        verify(fileStorageUseCase, times(1)).uploadChunk(fileId, chunkStream, chunkSize);
    }

    @Test
    void completeUpload_ShouldReturn200Ok() {
        // Given
        UUID fileId = UUID.randomUUID();
        FileMetadataContext mockContext = new FileMetadataContext(fileId, "video.mp4", "video/mp4", 1024L, UploadStatus.COMPLETED, Instant.now());
        when(fileStorageUseCase.completeUpload(fileId)).thenReturn(mockContext);

        // When
        Response response = resource.completeUpload(fileId);

        // Then
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(mockContext, response.getEntity());
        verify(fileStorageUseCase, times(1)).completeUpload(fileId);
    }
}
