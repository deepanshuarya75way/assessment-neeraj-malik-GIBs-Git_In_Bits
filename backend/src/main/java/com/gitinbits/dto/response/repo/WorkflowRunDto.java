package com.gitinbits.dto.response.repo;

public record WorkflowRunDto(
        String name,
        String branch,
        String status,
        String conclusion,
        String event,
        String createdAt,
        String updatedAt
) {}
