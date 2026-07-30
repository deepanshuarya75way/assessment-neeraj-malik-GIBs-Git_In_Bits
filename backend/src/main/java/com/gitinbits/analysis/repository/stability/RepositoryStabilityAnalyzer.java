package com.gitinbits.analysis.repository.stability;

import com.gitinbits.analysis.common.Analyzer;
import com.gitinbits.analysis.repository.RepositoryAnalysisContext;
import com.gitinbits.analysis.repository.stability.scorer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class RepositoryStabilityAnalyzer implements Analyzer<RepositoryAnalysisContext, RepositoryStabilityResult> {

    private static final Logger log = LoggerFactory.getLogger(RepositoryStabilityAnalyzer.class);

    private final CommitStabilityScorer commitStabilityScorer;
    private final ReleaseStabilityScorer releaseStabilityScorer;
    private final BranchStabilityScorer branchStabilityScorer;
    private final WorkflowStabilityScorer workflowStabilityScorer;
    private final DeploymentStabilityScorer deploymentStabilityScorer;
    private final ContributorStabilityScorer contributorStabilityScorer;
    private final RepositoryStabilityInsightGenerator insightGenerator;

    public RepositoryStabilityAnalyzer(
            CommitStabilityScorer commitStabilityScorer,
            ReleaseStabilityScorer releaseStabilityScorer,
            BranchStabilityScorer branchStabilityScorer,
            WorkflowStabilityScorer workflowStabilityScorer,
            DeploymentStabilityScorer deploymentStabilityScorer,
            ContributorStabilityScorer contributorStabilityScorer,
            RepositoryStabilityInsightGenerator insightGenerator
    ) {
        this.commitStabilityScorer = commitStabilityScorer;
        this.releaseStabilityScorer = releaseStabilityScorer;
        this.branchStabilityScorer = branchStabilityScorer;
        this.workflowStabilityScorer = workflowStabilityScorer;
        this.deploymentStabilityScorer = deploymentStabilityScorer;
        this.contributorStabilityScorer = contributorStabilityScorer;
        this.insightGenerator = insightGenerator;
    }

    @Override
    public RepositoryStabilityResult analyze(RepositoryAnalysisContext context) {
        int commitScore = commitStabilityScorer.score(context.commits());
        int branchScore = branchStabilityScorer.score(context.branches());
        int contributorScore = contributorStabilityScorer.score(context.contributors());
        
        // Pass empty lists for metrics not currently in the context to avoid modifying existing classes
        int releaseScore = releaseStabilityScorer.score(Collections.emptyList());
        int workflowScore = workflowStabilityScorer.score(Collections.emptyList());
        int deploymentScore = deploymentStabilityScorer.score(Collections.emptyList());

        StabilityScoreWeights weights = StabilityScoreWeights.defaultWeights();

        double weightedSum = (commitScore * weights.commitWeight())
                + (releaseScore * weights.releaseWeight())
                + (branchScore * weights.branchWeight())
                + (workflowScore * weights.workflowWeight())
                + (deploymentScore * weights.deploymentWeight())
                + (contributorScore * weights.contributorWeight());

        int overallScore = (int) Math.round(weightedSum);
        overallScore = Math.max(0, Math.min(100, overallScore));

        StabilityLevel stabilityLevel = StabilityLevel.fromScore(overallScore);

        if (log.isDebugEnabled()) {
            log.debug("Running Repository Stability Analysis");
            log.debug("Commit Stability = {}", commitScore);
            log.debug("Release Stability = {}", releaseScore);
            log.debug("Branch Stability = {}", branchScore);
            log.debug("Workflow Stability = {}", workflowScore);
            log.debug("Deployment Stability = {}", deploymentScore);
            log.debug("Contributor Stability = {}", contributorScore);
            log.debug("Overall Stability = {}", overallScore);
        }

        RepositoryStabilityBreakdown breakdown = new RepositoryStabilityBreakdown(
                commitScore,
                releaseScore,
                branchScore,
                workflowScore,
                deploymentScore,
                contributorScore,
                overallScore
        );

        java.util.List<RepositoryStabilityInsight> strengths = insightGenerator.generateStrengths(breakdown);
        java.util.List<RepositoryStabilityInsight> concerns = insightGenerator.generateConcerns(breakdown);
        java.util.List<RepositoryStabilityInsight> recommendations = insightGenerator.generateRecommendations(breakdown);

        if (log.isDebugEnabled()) {
            log.debug("Generated {} strengths", strengths.size());
            log.debug("Generated {} concerns", concerns.size());
            log.debug("Generated {} recommendations", recommendations.size());
        }

        return new RepositoryStabilityResult(
                breakdown,
                overallScore,
                stabilityLevel,
                strengths,
                concerns,
                recommendations
        );
    }
}
