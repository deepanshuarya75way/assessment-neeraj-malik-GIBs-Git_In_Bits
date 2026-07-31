package com.gitinbits.dto.response.repo;

public record DeveloperActivitySummaryDto(
    String id, // Author Name from group _id
    long commitCount,
    String latestCommitDate,
    String latestCommitMessage
) {}
