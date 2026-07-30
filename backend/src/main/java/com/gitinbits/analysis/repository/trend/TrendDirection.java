package com.gitinbits.analysis.repository.trend;

/**
 * Categorical classification of engineering trend direction.
 */
public enum TrendDirection {
    IMPROVING,
    STABLE,
    DECLINING;

    /**
     * Maps a 0-100 numeric score to a TrendDirection.
     * High scores indicate positive/improving trends.
     *
     * @param score Numeric score from 0 to 100.
     * @return Corresponding TrendDirection.
     */
    public static TrendDirection fromScore(int score) {
        if (score > 70) return IMPROVING;
        if (score <= 40) return DECLINING;
        return STABLE;
    }
}
