package com.gitinbits.analysis.repository.stability;

import java.util.List;

public record RepositoryStabilityResult(
        RepositoryStabilityBreakdown breakdown,
        int overallScore,
        StabilityLevel stabilityLevel,
        List<RepositoryStabilityInsight> strengths,
        List<RepositoryStabilityInsight> concerns,
        List<RepositoryStabilityInsight> recommendations
) {
    public RepositoryStabilityResult {
        strengths = strengths != null ? List.copyOf(strengths) : List.of();
        concerns = concerns != null ? List.copyOf(concerns) : List.of();
        recommendations = recommendations != null ? List.copyOf(recommendations) : List.of();
    }
}
