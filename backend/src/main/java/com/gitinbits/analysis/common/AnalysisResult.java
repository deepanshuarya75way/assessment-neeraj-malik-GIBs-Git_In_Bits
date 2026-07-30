package com.gitinbits.analysis.common;

import java.time.Instant;

/**
 * Base interface for all analysis results produced by the Git in Bits analysis layer.
 */
public interface AnalysisResult {
    /**
     * @return The overall score (0-100) calculated by the analysis engine.
     */
    int overallScore();

    /**
     * @return The timestamp when this analysis was evaluated.
     */
    Instant evaluationTimestamp();
}
