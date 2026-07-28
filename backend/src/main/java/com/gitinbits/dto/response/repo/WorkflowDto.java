package com.gitinbits.dto.response.repo;

public record WorkflowDto(
        String name,
        String state,
        String path,
        String createdAt,
        String updatedAt
) {}
