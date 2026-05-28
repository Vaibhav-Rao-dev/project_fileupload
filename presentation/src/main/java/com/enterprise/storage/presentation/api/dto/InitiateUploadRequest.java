package com.enterprise.storage.presentation.api.dto;

public record InitiateUploadRequest(
    String fileName,
    String contentType,
    long sizeBytes
) {}
