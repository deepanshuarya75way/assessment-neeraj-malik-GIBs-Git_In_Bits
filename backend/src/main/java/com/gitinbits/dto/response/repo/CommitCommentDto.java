package com.gitinbits.dto.response.repo;

public record CommitCommentDto(
        Long id,
        String author,
        String body,
        String createdAt,
        String updatedAt
) {}
