package com.gitinbits.analysis.common;

/**
 * Categorical classification of health status based on overall numeric score.
 */
public enum HealthLevel {
    EXCELLENT,
    GOOD,
    FAIR,
    POOR,
    CRITICAL;

    /**
     * Maps a 0-100 numeric score to a categorical HealthLevel.
     *
     * @param score Numeric score from 0 to 100.
     * @return Corresponding HealthLevel.
     */
    public static HealthLevel fromScore(int score) {
        if (score >= 90) return EXCELLENT;
        if (score >= 75) return GOOD;
        if (score >= 60) return FAIR;
        if (score >= 40) return POOR;
        return CRITICAL;
    }
}
