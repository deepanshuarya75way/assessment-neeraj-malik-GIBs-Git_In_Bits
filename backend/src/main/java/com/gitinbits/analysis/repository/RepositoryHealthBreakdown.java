package com.gitinbits.analysis.repository;

/**
 * Detailed breakdown of individual component health scores contributing to overall repository health.
 *
 * @param commitScore       Numeric health score for commits (0-100)
 * @param pullRequestScore  Numeric health score for pull requests (0-100)
 * @param issueScore        Numeric health score for issues (0-100)
 * @param reviewScore       Numeric health score for reviews (0-100)
 * @param branchScore       Numeric health score for branches (0-100)
 * @param contributorScore  Numeric health score for contributors (0-100)
 * @param overallScore      Aggregated overall health score (0-100)
 */
public record RepositoryHealthBreakdown(
        int commitScore,
        int pullRequestScore,
        int issueScore,
        int reviewScore,
        int branchScore,
        int contributorScore,
        int overallScore
) {}
