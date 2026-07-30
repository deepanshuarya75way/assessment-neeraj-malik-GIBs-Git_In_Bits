package com.gitinbits.analysis.repository.stability;

public record RepositoryStabilityBreakdown(
        int commitStabilityScore,
        int releaseStabilityScore,
        int branchStabilityScore,
        int workflowStabilityScore,
        int deploymentStabilityScore,
        int contributorStabilityScore,
        int overallScore
) {}
