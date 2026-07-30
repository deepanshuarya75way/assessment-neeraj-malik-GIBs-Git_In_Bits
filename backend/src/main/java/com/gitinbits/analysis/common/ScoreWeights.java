package com.gitinbits.analysis.common;

/**
 * Defines the weights for aggregating individual health scores into an overall repository health score.
 * Weights should sum to 1.0.
 *
 * @param commitWeight      Weight for commit score
 * @param pullRequestWeight Weight for pull request score
 * @param issueWeight       Weight for issue score
 * @param reviewWeight      Weight for review score
 * @param branchWeight      Weight for branch score
 * @param contributorWeight Weight for contributor score
 */
public record ScoreWeights(
        double commitWeight,
        double pullRequestWeight,
        double issueWeight,
        double reviewWeight,
        double branchWeight,
        double contributorWeight
) {
    /**
     * Provides default standard weights for repository health aggregation.
     *
     * @return Standard ScoreWeights totaling 1.0.
     */
    public static ScoreWeights defaultWeights() {
        return new ScoreWeights(0.25, 0.20, 0.15, 0.15, 0.15, 0.10);
    }
}
