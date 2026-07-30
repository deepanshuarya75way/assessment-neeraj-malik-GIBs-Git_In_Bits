package com.gitinbits.analysis.repository.risk;

public record RiskScoreWeights(
        double commitWeight,
        double pullRequestWeight,
        double issueWeight,
        double reviewWeight,
        double branchWeight,
        double releaseWeight
) {
    public static RiskScoreWeights defaultWeights() {
        return new RiskScoreWeights(0.20, 0.20, 0.15, 0.15, 0.20, 0.10);
    }
}
