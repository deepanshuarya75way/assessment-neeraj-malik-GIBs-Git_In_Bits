package com.gitinbits.dto.response.analysis;

import com.gitinbits.analysis.repository.RepositoryHealthResult;

import com.gitinbits.analysis.repository.stability.RepositoryStabilityResult;
import com.gitinbits.analysis.repository.risk.RepositoryRiskResult;
import com.gitinbits.analysis.repository.trend.RepositoryTrendResult;

/**
 * Combined report representing the complete Repository Analysis domain.
 *
 * <p>Although only {@code health} is currently available in V1, future versions will extend this report
 * with additional analysis modules (e.g., risk, stability, trends, recommendations, AI summary)
 * without requiring API endpoint redesign or breaking backwards compatibility for existing clients.
 *
 * @param health The repository health evaluation result
 * @param stability The repository stability evaluation result
 * @param risk The repository risk evaluation result
 * @param trend The repository trend evaluation result
 */
public record RepositoryAnalysisReport(
        RepositoryHealthResult health,
        RepositoryStabilityResult stability,
        RepositoryRiskResult risk,
        RepositoryTrendResult trend
) {}
