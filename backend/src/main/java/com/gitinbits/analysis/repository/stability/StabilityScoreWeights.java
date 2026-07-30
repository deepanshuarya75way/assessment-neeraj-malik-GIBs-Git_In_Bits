package com.gitinbits.analysis.repository.stability;

public record StabilityScoreWeights(
        double commitWeight,
        double releaseWeight,
        double branchWeight,
        double workflowWeight,
        double deploymentWeight,
        double contributorWeight
) {
    public static StabilityScoreWeights defaultWeights() {
        return new StabilityScoreWeights(0.25, 0.20, 0.20, 0.15, 0.10, 0.10);
    }
}
