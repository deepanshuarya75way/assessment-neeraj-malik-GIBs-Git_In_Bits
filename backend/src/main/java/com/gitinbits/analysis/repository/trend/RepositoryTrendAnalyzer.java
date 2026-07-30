package com.gitinbits.analysis.repository.trend;

import com.gitinbits.analysis.common.Analyzer;
import com.gitinbits.analysis.repository.RepositoryAnalysisContext;
import com.gitinbits.analysis.repository.trend.scorer.CommitTrendScorer;
import com.gitinbits.analysis.repository.trend.scorer.ContributorTrendScorer;
import com.gitinbits.analysis.repository.trend.scorer.IssueTrendScorer;
import com.gitinbits.analysis.repository.trend.scorer.PullRequestTrendScorer;
import com.gitinbits.analysis.repository.trend.scorer.ReleaseTrendScorer;
import com.gitinbits.analysis.repository.trend.scorer.ReviewTrendScorer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class RepositoryTrendAnalyzer implements Analyzer<RepositoryAnalysisContext, RepositoryTrendResult> {

    private static final Logger log = LoggerFactory.getLogger(RepositoryTrendAnalyzer.class);

    private final CommitTrendScorer commitTrendScorer;
    private final PullRequestTrendScorer pullRequestTrendScorer;
    private final IssueTrendScorer issueTrendScorer;
    private final ReviewTrendScorer reviewTrendScorer;
    private final ContributorTrendScorer contributorTrendScorer;
    private final ReleaseTrendScorer releaseTrendScorer;

    public RepositoryTrendAnalyzer(
            CommitTrendScorer commitTrendScorer,
            PullRequestTrendScorer pullRequestTrendScorer,
            IssueTrendScorer issueTrendScorer,
            ReviewTrendScorer reviewTrendScorer,
            ContributorTrendScorer contributorTrendScorer,
            ReleaseTrendScorer releaseTrendScorer
    ) {
        this.commitTrendScorer = commitTrendScorer;
        this.pullRequestTrendScorer = pullRequestTrendScorer;
        this.issueTrendScorer = issueTrendScorer;
        this.reviewTrendScorer = reviewTrendScorer;
        this.contributorTrendScorer = contributorTrendScorer;
        this.releaseTrendScorer = releaseTrendScorer;
    }

    @Override
    public RepositoryTrendResult analyze(RepositoryAnalysisContext context) {
        if (log.isDebugEnabled()) {
            log.debug("Running Repository Trend Analysis");
        }

        int commitTrend = commitTrendScorer.score(context.commits());
        int pullRequestTrend = pullRequestTrendScorer.score(context.pullRequests());
        int issueTrend = issueTrendScorer.score(context.issues());
        int reviewTrend = reviewTrendScorer.score(context.reviews());
        int contributorTrend = contributorTrendScorer.score(context.contributors());
        int releaseTrend = releaseTrendScorer.score(java.util.Collections.emptyList());

        if (log.isDebugEnabled()) {
            log.debug("Trend Scores - Commits: {}, PRs: {}, Issues: {}, Reviews: {}, Contributors: {}, Releases: {}",
                    commitTrend, pullRequestTrend, issueTrend, reviewTrend, contributorTrend, releaseTrend);
        }

        TrendScoreWeights weights = TrendScoreWeights.defaultWeights();

        double weightedScore =
                (commitTrend * weights.commitWeight()) +
                (pullRequestTrend * weights.pullRequestWeight()) +
                (issueTrend * weights.issueWeight()) +
                (reviewTrend * weights.reviewWeight()) +
                (contributorTrend * weights.contributorWeight()) +
                (releaseTrend * weights.releaseWeight());

        int overallTrend = (int) Math.max(0, Math.min(100, Math.round(weightedScore)));
        TrendDirection direction = TrendDirection.fromScore(overallTrend);

        if (log.isDebugEnabled()) {
            log.debug("Overall Trend Score: {} ({})", overallTrend, direction);
        }

        RepositoryTrendBreakdown breakdown = new RepositoryTrendBreakdown(
                commitTrend,
                pullRequestTrend,
                issueTrend,
                reviewTrend,
                contributorTrend,
                releaseTrend,
                overallTrend
        );

        return new RepositoryTrendResult(
                breakdown,
                overallTrend,
                direction,
                Instant.now()
        );
    }
}
