package com.enterprise.storage.domain.model;

/**
 * Tracks the state machine of a multipart file upload.
 */
public enum UploadStatus {
    INITIATED,
    UPLOADING,
    COMPLETED,
    FAILED
}
