package com.gitinbits.analysis.repository;

import com.gitinbits.analysis.common.*;
import com.gitinbits.analysis.repository.finding.*;
import com.gitinbits.analysis.repository.scorer.*;
import com.gitinbits.dto.response.analysis.HealthFinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrator class for repository health analysis.
 * Implements the generic Analyzer interface for RepositoryAnalysisContext.
 *
 * <p>This class contains NO health formulas. It strictly orchestrates independent scorer classes,
 * finding generators, and aggregates their outputs into a final RepositoryHealthResult.
 */
@Component
public class RepositoryHealthAnalyzer implements Analyzer<RepositoryAnalysisContext, RepositoryHealthResult> {

    private static final Logger log = LoggerFactory.getLogger(RepositoryHealthAnalyzer.class);

    private final CommitHealthScorer commitHealthScorer;
    private final PullRequestHealthScorer pullRequestHealthScorer;
    private final IssueHealthScorer issueHealthScorer;
    private final ReviewHealthScorer reviewHealthScorer;
    private final BranchHealthScorer branchHealthScorer;
    private final ContributorHealthScorer contributorHealthScorer;

    private final CommitFindingGenerator commitFindingGenerator;
    private final PullRequestFindingGenerator pullRequestFindingGenerator;
    private final IssueFindingGenerator issueFindingGenerator;
    private final ReviewFindingGenerator reviewFindingGenerator;
    private final BranchFindingGenerator branchFindingGenerator;
    private final ContributorFindingGenerator contributorFindingGenerator;

    public RepositoryHealthAnalyzer(
            CommitHealthScorer commitHealthScorer,
            PullRequestHealthScorer pullRequestHealthScorer,
            IssueHealthScorer issueHealthScorer,
            ReviewHealthScorer reviewHealthScorer,
            BranchHealthScorer branchHealthScorer,
            ContributorHealthScorer contributorHealthScorer,
            CommitFindingGenerator commitFindingGenerator,
            PullRequestFindingGenerator pullRequestFindingGenerator,
            IssueFindingGenerator issueFindingGenerator,
            ReviewFindingGenerator reviewFindingGenerator,
            BranchFindingGenerator branchFindingGenerator,
            ContributorFindingGenerator contributorFindingGenerator
    ) {
        this.commitHealthScorer = commitHealthScorer;
        this.pullRequestHealthScorer = pullRequestHealthScorer;
        this.issueHealthScorer = issueHealthScorer;
        this.reviewHealthScorer = reviewHealthScorer;
        this.branchHealthScorer = branchHealthScorer;
        this.contributorHealthScorer = contributorHealthScorer;
        
        this.commitFindingGenerator = commitFindingGenerator;
        this.pullRequestFindingGenerator = pullRequestFindingGenerator;
        this.issueFindingGenerator = issueFindingGenerator;
        this.reviewFindingGenerator = reviewFindingGenerator;
        this.branchFindingGenerator = branchFindingGenerator;
        this.contributorFindingGenerator = contributorFindingGenerator;
    }

    @Override
    public RepositoryHealthResult analyze(RepositoryAnalysisContext context) {
        // 1. Generate Scores
        int commitScore = commitHealthScorer.score(context.commits());
        int prScore = pullRequestHealthScorer.score(context.pullRequests());
        int issueScore = issueHealthScorer.score(context.issues());
        int reviewScore = reviewHealthScorer.score(context.reviews());
        int branchScore = branchHealthScorer.score(context.branches());
        int contributorScore = contributorHealthScorer.score(context.contributors());

        ScoreWeights weights = ScoreWeights.defaultWeights();

        double weightedSum = (commitScore * weights.commitWeight())
                + (prScore * weights.pullRequestWeight())
                + (issueScore * weights.issueWeight())
                + (reviewScore * weights.reviewWeight())
                + (branchScore * weights.branchWeight())
                + (contributorScore * weights.contributorWeight());

        int overallScore = (int) Math.round(weightedSum);
        overallScore = Math.max(AnalysisConstants.MIN_SCORE, Math.min(AnalysisConstants.MAX_SCORE, overallScore));

        RepositoryHealthBreakdown breakdown = new RepositoryHealthBreakdown(
                commitScore,
                prScore,
                issueScore,
                reviewScore,
                branchScore,
                contributorScore,
                overallScore
        );

        HealthLevel healthLevel = HealthLevel.fromScore(overallScore);
        String repositoryName = (context.repo() != null && context.repo().name() != null)
                ? context.repo().name() : "unknown-repository";

        // 2. Generate Findings
        List<HealthFinding> allFindings = new ArrayList<>();
        allFindings.addAll(commitFindingGenerator.generate(context, commitScore));
        allFindings.addAll(pullRequestFindingGenerator.generate(context, prScore));
        allFindings.addAll(issueFindingGenerator.generate(context, issueScore));
        allFindings.addAll(reviewFindingGenerator.generate(context, reviewScore));
        allFindings.addAll(branchFindingGenerator.generate(context, branchScore));
        allFindings.addAll(contributorFindingGenerator.generate(context, contributorScore));

        if (log.isDebugEnabled()) {
            log.debug("Generated {} health findings", allFindings.size());
            log.debug("Repository score: {}", overallScore);
            log.debug("Health level: {}", healthLevel);
        }

        return new RepositoryHealthResult(
                repositoryName,
                overallScore,
                healthLevel,
                breakdown,
                allFindings,
                Instant.now()
        );
    }
}
