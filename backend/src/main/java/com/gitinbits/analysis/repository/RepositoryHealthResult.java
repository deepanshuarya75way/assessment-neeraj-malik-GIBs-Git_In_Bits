package com.gitinbits.analysis.repository;

import com.gitinbits.analysis.common.AnalysisResult;
import com.gitinbits.analysis.common.HealthLevel;
import com.gitinbits.dto.response.analysis.HealthFinding;

import java.time.Instant;
import java.util.List;

/**
 * Result of executing repository health analysis.
 *
 * @param repositoryName      Name of the evaluated repository
 * @param overallScore        Aggregated overall numeric health score (0-100)
 * @param healthLevel         Categorical health status classification
 * @param breakdown           Detailed score breakdown across analysis dimensions
 * @param findings            Human-readable findings generated from the analysis
 * @param evaluationTimestamp Timestamp when this evaluation was completed
 */
public record RepositoryHealthResult(
        String repositoryName,
        int overallScore,
        HealthLevel healthLevel,
        RepositoryHealthBreakdown breakdown,
        List<HealthFinding> findings,
        Instant evaluationTimestamp
) implements AnalysisResult {}

