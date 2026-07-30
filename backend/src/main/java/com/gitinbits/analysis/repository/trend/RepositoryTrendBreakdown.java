package com.gitinbits.analysis.repository.trend;

/**
 * Breakdown of individual engineering trend scores.
 * All scores are 0-100.
 */
public record RepositoryTrendBreakdown(
        int commitTrend,
        int pullRequestTrend,
        int issueTrend,
        int reviewTrend,
        int contributorTrend,
        int releaseTrend,
        int overallTrend
) {}
