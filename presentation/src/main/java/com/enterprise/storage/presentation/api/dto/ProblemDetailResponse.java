package com.enterprise.storage.presentation.api.dto;

public record ProblemDetailResponse(
    String type,
    String title,
    int status,
    String detail,
    String instance
) {}
