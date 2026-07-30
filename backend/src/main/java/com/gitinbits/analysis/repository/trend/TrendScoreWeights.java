package com.gitinbits.analysis.repository.trend;

/**
 * Immutable weights for calculating overall repository trend.
 */
public record TrendScoreWeights(
        double commitWeight,
        double pullRequestWeight,
        double issueWeight,
        double reviewWeight,
        double contributorWeight,
        double releaseWeight
) {
    /**
     * Provides standard balanced default weights for trend analysis.
     * Weights should total 1.0 (100%).
     */
    public static TrendScoreWeights defaultWeights() {
        return new TrendScoreWeights(
                0.25, // Commits
                0.25, // Pull Requests
                0.15, // Issues
                0.15, // Reviews
                0.10, // Contributors
                0.10  // Releases
        );
    }
}
