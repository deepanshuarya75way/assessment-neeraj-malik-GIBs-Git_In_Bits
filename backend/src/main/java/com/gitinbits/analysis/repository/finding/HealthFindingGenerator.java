package com.gitinbits.analysis.repository.finding;

import com.gitinbits.analysis.repository.RepositoryAnalysisContext;
import com.gitinbits.dto.response.analysis.HealthFinding;

import java.util.List;

/**
 * Common interface for all finding generators in the repository health analysis layer.
 * A finding generator evaluates context and numeric scores to produce human-readable
 * findings, warnings, and recommendations.
 */
public interface HealthFindingGenerator {

    /**
     * Generates a list of health findings based on the analysis context and the numeric score.
     *
     * @param context The full repository analysis context
     * @param score   The numeric score calculated by the corresponding scorer (0-100)
     * @return A list of human-readable findings
     */
    List<HealthFinding> generate(RepositoryAnalysisContext context, int score);
}
