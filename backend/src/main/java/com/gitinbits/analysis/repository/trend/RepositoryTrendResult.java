package com.gitinbits.analysis.repository.trend;

import com.gitinbits.analysis.common.AnalysisResult;
import java.time.Instant;

/**
 * The final immutable result of the Repository Trend Analysis.
 */
public record RepositoryTrendResult(
        RepositoryTrendBreakdown breakdown,
        int overallTrend,
        TrendDirection direction,
        Instant evaluationTimestamp
) implements AnalysisResult {
    @Override
    public int overallScore() {
        return overallTrend;
    }
}
