package com.enterprise.storage.domain.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Immutable domain data carrier for file metadata, now state-aware.
 */
public record FileMetadataContext(
    UUID id,
    String fileName,
    String contentType,
    Long sizeBytes,
    UploadStatus status,
    Instant createdAt
) {}
