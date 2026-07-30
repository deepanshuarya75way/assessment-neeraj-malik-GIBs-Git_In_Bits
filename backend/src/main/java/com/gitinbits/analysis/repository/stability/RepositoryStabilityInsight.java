package com.gitinbits.analysis.repository.stability;

public record RepositoryStabilityInsight(
        String title,
        String description,
        InsightSeverity severity
) {}
