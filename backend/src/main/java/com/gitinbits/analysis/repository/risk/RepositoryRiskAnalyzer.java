package com.gitinbits.analysis.repository.risk;

import com.gitinbits.analysis.common.Analyzer;
import com.gitinbits.analysis.common.HealthLevel;
import com.gitinbits.analysis.repository.RepositoryAnalysisContext;
import com.gitinbits.analysis.repository.risk.scorer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.time.Instant;

@Component
public class RepositoryRiskAnalyzer implements Analyzer<RepositoryAnalysisContext, RepositoryRiskResult> {

    private static final Logger log = LoggerFactory.getLogger(RepositoryRiskAnalyzer.class);

    private final CommitRiskScorer commitRiskScorer;
    private final PullRequestRiskScorer pullRequestRiskScorer;
    private final IssueRiskScorer issueRiskScorer;
    private final ReviewRiskScorer reviewRiskScorer;
    private final BranchRiskScorer branchRiskScorer;
    private final ReleaseRiskScorer releaseRiskScorer;

    public RepositoryRiskAnalyzer(
            CommitRiskScorer commitRiskScorer,
            PullRequestRiskScorer pullRequestRiskScorer,
            IssueRiskScorer issueRiskScorer,
            ReviewRiskScorer reviewRiskScorer,
            BranchRiskScorer branchRiskScorer,
            ReleaseRiskScorer releaseRiskScorer
    ) {
        this.commitRiskScorer = commitRiskScorer;
        this.pullRequestRiskScorer = pullRequestRiskScorer;
        this.issueRiskScorer = issueRiskScorer;
        this.reviewRiskScorer = reviewRiskScorer;
        this.branchRiskScorer = branchRiskScorer;
        this.releaseRiskScorer = releaseRiskScorer;
    }

    @Override
    public RepositoryRiskResult analyze(RepositoryAnalysisContext context) {
        int commitScore = commitRiskScorer.score(context.commits());
        int prScore = pullRequestRiskScorer.score(context.pullRequests());
        int issueScore = issueRiskScorer.score(context.issues());
        int reviewScore = reviewRiskScorer.score(context.reviews());
        int branchScore = branchRiskScorer.score(context.branches());
        int releaseScore = releaseRiskScorer.score(java.util.Collections.emptyList());

        RiskScoreWeights weights = RiskScoreWeights.defaultWeights();

        double weightedSum = (commitScore * weights.commitWeight())
                + (prScore * weights.pullRequestWeight())
                + (issueScore * weights.issueWeight())
                + (reviewScore * weights.reviewWeight())
                + (branchScore * weights.branchWeight())
                + (releaseScore * weights.releaseWeight());

        int overallScore = (int) Math.round(weightedSum);
        overallScore = Math.max(0, Math.min(100, overallScore));

        RepositoryRiskBreakdown breakdown = new RepositoryRiskBreakdown(
                commitScore,
                prScore,
                issueScore,
                reviewScore,
                branchScore,
                releaseScore,
                overallScore
        );

        // Since Higher Score = Higher Risk, we invert it before deriving the standard HealthLevel
        // e.g., 90 Risk -> 10 Inverted -> CRITICAL HealthLevel
        int invertedForLevel = 100 - overallScore;
        HealthLevel riskLevel = HealthLevel.fromScore(invertedForLevel);

        if (log.isDebugEnabled()) {
            log.debug("Running Repository Risk Analysis");
            log.debug("Repository risk score: {}", overallScore);
            log.debug("Risk level: {}", riskLevel);
        }

        return new RepositoryRiskResult(
                breakdown,
                overallScore,
                riskLevel,
                Instant.now()
        );
    }
}
