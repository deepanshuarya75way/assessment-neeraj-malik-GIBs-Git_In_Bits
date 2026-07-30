package com.gitinbits.analysis.service;

import com.gitinbits.analysis.repository.RepositoryAnalysisContext;
import com.gitinbits.analysis.repository.RepositoryHealthAnalyzer;
import com.gitinbits.analysis.repository.RepositoryHealthResult;
import com.gitinbits.analysis.repository.stability.RepositoryStabilityAnalyzer;
import com.gitinbits.analysis.repository.stability.RepositoryStabilityResult;
import com.gitinbits.analysis.repository.builder.RepositoryContextBuilder;
import com.gitinbits.analysis.repository.risk.RepositoryRiskAnalyzer;
import com.gitinbits.analysis.repository.risk.RepositoryRiskResult;
import com.gitinbits.analysis.repository.trend.RepositoryTrendAnalyzer;
import com.gitinbits.analysis.repository.trend.RepositoryTrendResult;
import com.gitinbits.dto.context.GitHubContext;
import com.gitinbits.dto.response.analysis.RepositoryAnalysisReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Application orchestration service for all repository-level analysis workflows.
 *
 * <p>This service coordinates context building and analyzer execution without containing
 * business rules, scoring formulas, GitHub REST API coupling, or HTTP/controller logic.
 * It serves as the central entry point for future repository analysis dimensions
 * (Health, Risk, Stability, Trend, Recommendations, etc.).
 */
@Service
public class RepositoryAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(RepositoryAnalysisService.class);

    private final RepositoryContextBuilder repositoryContextBuilder;
    private final RepositoryHealthAnalyzer repositoryHealthAnalyzer;
    private final RepositoryStabilityAnalyzer repositoryStabilityAnalyzer;
    private final RepositoryRiskAnalyzer repositoryRiskAnalyzer;
    private final RepositoryTrendAnalyzer repositoryTrendAnalyzer;

    public RepositoryAnalysisService(
            RepositoryContextBuilder repositoryContextBuilder,
            RepositoryHealthAnalyzer repositoryHealthAnalyzer,
            RepositoryStabilityAnalyzer repositoryStabilityAnalyzer,
            RepositoryRiskAnalyzer repositoryRiskAnalyzer,
            RepositoryTrendAnalyzer repositoryTrendAnalyzer
    ) {
        this.repositoryContextBuilder = repositoryContextBuilder;
        this.repositoryHealthAnalyzer = repositoryHealthAnalyzer;
        this.repositoryStabilityAnalyzer = repositoryStabilityAnalyzer;
        this.repositoryRiskAnalyzer = repositoryRiskAnalyzer;
        this.repositoryTrendAnalyzer = repositoryTrendAnalyzer;
    }

    /**
     * Orchestrates complete repository analysis by gathering all analysis dimensions
     * into a unified {@link RepositoryAnalysisReport}.
     *
     * @param context        GitHub authentication/source context
     * @param repositoryName Target repository name
     * @return Completed RepositoryAnalysisReport containing all active analysis modules
     */
    public RepositoryAnalysisReport analyzeRepository(GitHubContext context, String repositoryName) {
        log.debug("Starting repository analysis for {}/{}", context.accountName(), repositoryName);
        
        RepositoryAnalysisContext analysisContext = repositoryContextBuilder.build(context, repositoryName);
        log.debug("Context successfully built for repo: {}/{}", context.accountName(), repositoryName);

        RepositoryHealthResult health = repositoryHealthAnalyzer.analyze(analysisContext);
        log.debug("Health analysis complete");
        
        RepositoryStabilityResult stability = repositoryStabilityAnalyzer.analyze(analysisContext);
        log.debug("Stability analysis complete");
        
        RepositoryRiskResult risk = repositoryRiskAnalyzer.analyze(analysisContext);
        log.debug("Risk analysis complete: overallRisk={}, riskLevel={}", risk.overallRisk(), risk.riskLevel());

        RepositoryTrendResult trend = repositoryTrendAnalyzer.analyze(analysisContext);
        log.debug("Trend analysis complete: overallTrend={}, direction={}", trend.overallTrend(), trend.direction());

        log.debug("Repository analysis finished");
        return new RepositoryAnalysisReport(health, stability, risk, trend);
    }

    /**
     * Orchestrates repository health evaluation by assembling context metadata
     * and delegating evaluation to the health analyzer.
     *
     * @param context        GitHub authentication/source context
     * @param repositoryName Target repository name
     * @return Completed RepositoryHealthResult
     */
    public RepositoryHealthResult analyzeRepositoryHealth(GitHubContext context, String repositoryName) {
        log.debug("Starting repository health analysis for {}/{}", context.accountName(), repositoryName);

        RepositoryAnalysisContext analysisContext = repositoryContextBuilder.build(context, repositoryName);
        log.debug("Context successfully built for repo: {}/{}", context.accountName(), repositoryName);

        RepositoryHealthResult result = repositoryHealthAnalyzer.analyze(analysisContext);
        log.debug("Health analysis completed for {}/{}: overallScore={}, healthLevel={}",
                context.accountName(), repositoryName, result.overallScore(), result.healthLevel());

        return result;
    }

    /**
     * Orchestrates repository stability evaluation by assembling context metadata
     * and delegating evaluation to the stability analyzer.
     *
     * @param context        GitHub authentication/source context
     * @param repositoryName Target repository name
     * @return Completed RepositoryStabilityResult
     */
    public RepositoryStabilityResult analyzeRepositoryStability(GitHubContext context, String repositoryName) {
        log.debug("Repository Stability Analysis Started");

        RepositoryAnalysisContext analysisContext = repositoryContextBuilder.build(context, repositoryName);
        log.debug("Repository Context Built");

        RepositoryStabilityResult result = repositoryStabilityAnalyzer.analyze(analysisContext);
        log.debug("Repository Stability Completed");

        return result;
    }
}
