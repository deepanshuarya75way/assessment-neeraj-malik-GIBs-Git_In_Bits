package com.gitinbits.analysis.repository.stability;

public enum StabilityLevel {
    VERY_STABLE,
    STABLE,
    MODERATE,
    UNSTABLE,
    CRITICAL;

    public static StabilityLevel fromScore(int score) {
        if (score >= 95) return VERY_STABLE;
        if (score >= 80) return STABLE;
        if (score >= 65) return MODERATE;
        if (score >= 45) return UNSTABLE;
        return CRITICAL;
    }
}
