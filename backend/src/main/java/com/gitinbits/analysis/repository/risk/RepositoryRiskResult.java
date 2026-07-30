package com.gitinbits.analysis.repository.risk;

import com.gitinbits.analysis.common.AnalysisResult;
import com.gitinbits.analysis.common.HealthLevel;
import java.time.Instant;

public record RepositoryRiskResult(
        RepositoryRiskBreakdown breakdown,
        int overallRisk,
        HealthLevel riskLevel,
        Instant evaluationTimestamp
) implements AnalysisResult {
    @Override
    public int overallScore() {
        return overallRisk;
    }
}
