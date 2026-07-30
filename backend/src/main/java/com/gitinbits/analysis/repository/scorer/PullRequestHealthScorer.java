package com.gitinbits.analysis.repository.scorer;

import com.gitinbits.dto.response.repo.PullRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/**
 * Evaluates the health of repository pull requests.
 * Independent scorer with single responsibility: only evaluates pull requests.
 */
@Component
public class PullRequestHealthScorer {

    private static final Logger log = LoggerFactory.getLogger(PullRequestHealthScorer.class);

    private static final int DEFAULT_EMPTY_SCORE = 50;

    // Weights
    private static final double WEIGHT_MERGE_RATE = 0.30;
    private static final double WEIGHT_REVIEW_PARTICIPATION = 0.25;
    private static final double WEIGHT_DISCUSSION_QUALITY = 0.15;
    private static final double WEIGHT_PR_SIZE = 0.15;
    private static final double WEIGHT_DRAFT_RATIO = 0.10;
    private static final double WEIGHT_FRESHNESS = 0.05;

    // Thresholds
    private static final double IDEAL_REVIEWS_PER_PR = 3.0;
    private static final double IDEAL_COMMENTS_LOWER = 2.0;
    private static final double IDEAL_COMMENTS_UPPER = 10.0;
    private static final double PENALTY_COMMENTS_THRESHOLD = 20.0;
    private static final double IDEAL_LINES_CHANGED = 300.0;
    private static final double MAX_LINES_CHANGED = 1000.0;
    private static final double IDEAL_CHANGED_FILES = 15.0;
    private static final double MAX_CHANGED_FILES = 50.0;

    /**
     * Calculates numeric health score (0-100) based on PR volume, merge ratio, size, reviews, etc.
     *
     * @param pullRequests List of repository pull requests
     * @return Numeric score between 0 and 100
     */
    public int score(List<PullRequestDto> pullRequests) {
        if (pullRequests == null || pullRequests.isEmpty()) {
            return DEFAULT_EMPTY_SCORE;
        }

        int mergeRateScore = calculateMergeRate(pullRequests);
        int reviewScore = calculateReviewScore(pullRequests);
        int discussionScore = calculateDiscussionScore(pullRequests);
        int prSizeScore = calculatePrSizeScore(pullRequests);
        int draftScore = calculateDraftPenalty(pullRequests);
        int freshnessScore = calculateFreshnessScore(pullRequests);

        double finalScore = (mergeRateScore * WEIGHT_MERGE_RATE)
                + (reviewScore * WEIGHT_REVIEW_PARTICIPATION)
                + (discussionScore * WEIGHT_DISCUSSION_QUALITY)
                + (prSizeScore * WEIGHT_PR_SIZE)
                + (draftScore * WEIGHT_DRAFT_RATIO)
                + (freshnessScore * WEIGHT_FRESHNESS);

        int clampedScore = clamp((int) Math.round(finalScore));

        if (log.isDebugEnabled()) {
            log.debug("PullRequestHealthScorer - Merge Rate Score: {}", mergeRateScore);
            log.debug("PullRequestHealthScorer - Review Score: {}", reviewScore);
            log.debug("PullRequestHealthScorer - Discussion Score: {}", discussionScore);
            log.debug("PullRequestHealthScorer - PR Size Score: {}", prSizeScore);
            log.debug("PullRequestHealthScorer - Draft Score: {}", draftScore);
            log.debug("PullRequestHealthScorer - Freshness Score: {}", freshnessScore);
            log.debug("PullRequestHealthScorer - Final Score: {}", clampedScore);
        }

        return clampedScore;
    }

    private int calculateMergeRate(List<PullRequestDto> prs) {
        long closed = 0;
        long merged = 0;
        for (PullRequestDto pr : prs) {
            if ("closed".equalsIgnoreCase(pr.state())) {
                closed++;
                if (pr.mergedAt() != null) {
                    merged++;
                }
            }
        }
        if (closed == 0) {
            return 100; // Do not penalize if there are only open PRs
        }
        double ratio = (double) merged / closed;
        return clamp((int) Math.round(ratio * 100));
    }

    private int calculateReviewScore(List<PullRequestDto> prs) {
        long totalReviews = 0;
        for (PullRequestDto pr : prs) {
            if (pr.reviewCount() != null) {
                totalReviews += pr.reviewCount();
            }
        }
        double avgReviews = (double) totalReviews / prs.size();
        double ratio = avgReviews / IDEAL_REVIEWS_PER_PR;
        return clamp((int) Math.round(ratio * 100));
    }

    private int calculateDiscussionScore(List<PullRequestDto> prs) {
        long totalComments = 0;
        for (PullRequestDto pr : prs) {
            if (pr.commentCount() != null) {
                totalComments += pr.commentCount();
            }
        }
        double avgComments = (double) totalComments / prs.size();

        if (avgComments >= IDEAL_COMMENTS_LOWER && avgComments <= IDEAL_COMMENTS_UPPER) {
            return 100;
        } else if (avgComments < IDEAL_COMMENTS_LOWER) {
            return clamp((int) Math.round((avgComments / IDEAL_COMMENTS_LOWER) * 80 + 20)); // Baseline of 20
        } else {
            if (avgComments >= PENALTY_COMMENTS_THRESHOLD) {
                return 40;
            }
            double ratio = (avgComments - IDEAL_COMMENTS_UPPER) / (PENALTY_COMMENTS_THRESHOLD - IDEAL_COMMENTS_UPPER);
            return clamp((int) Math.round(100 - (ratio * 60))); // Drops from 100 down to 40
        }
    }

    private int calculatePrSizeScore(List<PullRequestDto> prs) {
        long totalSize = 0;
        long totalFiles = 0;
        for (PullRequestDto pr : prs) {
            int add = pr.additions() != null ? pr.additions() : 0;
            int del = pr.deletions() != null ? pr.deletions() : 0;
            totalSize += (add + del);

            if (pr.changedFilesCount() != null) {
                totalFiles += pr.changedFilesCount();
            }
        }

        double avgSize = (double) totalSize / prs.size();
        double avgFiles = (double) totalFiles / prs.size();

        int sizeScore = 100;
        if (avgSize > IDEAL_LINES_CHANGED) {
            double penalty = Math.min(1.0, (avgSize - IDEAL_LINES_CHANGED) / (MAX_LINES_CHANGED - IDEAL_LINES_CHANGED));
            sizeScore = 100 - (int) Math.round(penalty * 60); // Drops to 40 max penalty
        }

        int fileScore = 100;
        if (avgFiles > IDEAL_CHANGED_FILES) {
            double penalty = Math.min(1.0, (avgFiles - IDEAL_CHANGED_FILES) / (MAX_CHANGED_FILES - IDEAL_CHANGED_FILES));
            fileScore = 100 - (int) Math.round(penalty * 60); // Drops to 40 max penalty
        }

        return (sizeScore + fileScore) / 2;
    }

    private int calculateDraftPenalty(List<PullRequestDto> prs) {
        long drafts = prs.stream().filter(pr -> Boolean.TRUE.equals(pr.draft())).count();
        double draftRatio = (double) drafts / prs.size();
        return clamp((int) Math.round(100 - (draftRatio * 70))); // 100% drafts reduces score to 30
    }

    private int calculateFreshnessScore(List<PullRequestDto> prs) {
        long maxTime = 0;
        for (PullRequestDto pr : prs) {
            long time = 0;
            if (pr.updatedAt() != null) {
                time = parseTime(pr.updatedAt());
            } else if (pr.createdAt() != null) {
                time = parseTime(pr.createdAt());
            }
            if (time > maxTime) {
                maxTime = time;
            }
        }

        if (maxTime == 0) {
            return 50;
        }

        long daysAgo = (System.currentTimeMillis() - maxTime) / (1000L * 60 * 60 * 24);
        if (daysAgo <= 7) return 100;
        if (daysAgo <= 30) return 80;
        if (daysAgo <= 90) return 50;
        return 20;
    }

    private long parseTime(String isoString) {
        try {
            return Instant.parse(isoString).toEpochMilli();
        } catch (Exception e) {
            return 0;
        }
    }

    private int clamp(int score) {
        return Math.max(0, Math.min(100, score));
    }
}
