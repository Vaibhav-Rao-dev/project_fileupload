package com.company.domain;

import java.util.UUID;

public interface FileStorageService {

    // Java Record: Immutable, high-performance data carrier ideal for SOLID DTO design
    record FileMetadataContext(
        UUID id,
        String fileName,
        String bucketName,
        long sizeBytes,
        String contentType
    ) {}

    /**
     * Uploads a raw binary stream to our object store and tracks its metadata.
     */
    FileMetadataContext processUpload(String fileName, String contentType, byte[] data);

    /**
     * Downloads a file's raw binary payload using its tracking identifier.
     */
    byte[] processDownload(UUID fileId);
}
