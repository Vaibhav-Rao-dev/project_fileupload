package com.enterprise.storage.domain.ports.api;

import com.enterprise.storage.domain.model.FileMetadataContext;
import java.io.InputStream;
import java.util.UUID;

/**
 * Primary API Port for external clients to interact with the storage domain.
 */
public interface FileStorageUseCase {
    FileMetadataContext initiateUpload(String fileName, String contentType, long expectedSizeBytes);
    void uploadChunk(UUID fileId, InputStream chunkStream, long chunkSize);
    FileMetadataContext completeUpload(UUID fileId);
}
