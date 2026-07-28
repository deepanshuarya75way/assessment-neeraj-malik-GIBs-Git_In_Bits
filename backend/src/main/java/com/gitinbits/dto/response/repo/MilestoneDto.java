package com.gitinbits.dto.response.repo;

public record MilestoneDto(
        String title,
        String description,
        String dueDate,
        String state,
        Integer openIssues,
        Integer closedIssues
) {}
