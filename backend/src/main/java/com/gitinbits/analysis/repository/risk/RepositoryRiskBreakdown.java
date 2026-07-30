package com.gitinbits.analysis.repository.risk;

public record RepositoryRiskBreakdown(
        int commitRisk,
        int pullRequestRisk,
        int issueRisk,
        int reviewRisk,
        int branchRisk,
        int releaseRisk,
        int overallRisk
) {}
