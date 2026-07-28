package com.gitinbits.dto.response.repo;

public record IssueCommentDto(
        Long id,
        String author,
        String comment,
        String createdAt,
        String updatedAt
) {}
