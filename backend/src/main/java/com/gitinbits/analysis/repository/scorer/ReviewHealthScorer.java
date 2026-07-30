package com.gitinbits.analysis.repository.scorer;

import com.gitinbits.dto.response.repo.ReviewDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/**
 * Evaluates the health of pull request reviews.
 * Independent scorer with single responsibility: only evaluates reviews.
 */
@Component
public class ReviewHealthScorer {

    private static final Logger log = LoggerFactory.getLogger(ReviewHealthScorer.class);

    private static final int DEFAULT_EMPTY_SCORE = 50;

    // Weights
    private static final double WEIGHT_PARTICIPATION = 0.30;
    private static final double WEIGHT_APPROVAL = 0.25;
    private static final double WEIGHT_CHANGE_REQUESTS = 0.20;
    private static final double WEIGHT_COMPLETION = 0.10;
    private static final double WEIGHT_FRESHNESS = 0.10;
    private static final double WEIGHT_DOCUMENTATION = 0.05;

    // Thresholds
    private static final double IDEAL_TOTAL_REVIEWS = 30.0;
    private static final double IDEAL_APPROVAL_RATIO_LOWER = 0.60;
    private static final double IDEAL_APPROVAL_RATIO_UPPER = 0.95;
    private static final double IDEAL_CHANGE_REQUEST_RATIO_LOWER = 0.05;
    private static final double IDEAL_CHANGE_REQUEST_RATIO_UPPER = 0.30;
    private static final int MINIMUM_REVIEW_BODY_LENGTH = 10;

    /**
     * Calculates numeric health score (0-100) based on review metadata.
     *
     * @param reviews List of pull request reviews
     * @return Numeric score between 0 and 100
     */
    public int score(List<ReviewDto> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return DEFAULT_EMPTY_SCORE;
        }

        int participationScore = calculateParticipationScore(reviews);
        int approvalScore = calculateApprovalScore(reviews);
        int changeRequestScore = calculateChangeRequestScore(reviews);
        int completionScore = calculateCompletionScore(reviews);
        int freshnessScore = calculateFreshnessScore(reviews);
        int documentationScore = calculateDocumentationScore(reviews);

        double finalScore = (participationScore * WEIGHT_PARTICIPATION)
                + (approvalScore * WEIGHT_APPROVAL)
                + (changeRequestScore * WEIGHT_CHANGE_REQUESTS)
                + (completionScore * WEIGHT_COMPLETION)
                + (freshnessScore * WEIGHT_FRESHNESS)
                + (documentationScore * WEIGHT_DOCUMENTATION);

        int clampedScore = clamp((int) Math.round(finalScore));

        if (log.isDebugEnabled()) {
            log.debug("ReviewHealthScorer - Participation Score: {}", participationScore);
            log.debug("ReviewHealthScorer - Approval Score: {}", approvalScore);
            log.debug("ReviewHealthScorer - Change Request Score: {}", changeRequestScore);
            log.debug("ReviewHealthScorer - Completion Score: {}", completionScore);
            log.debug("ReviewHealthScorer - Freshness Score: {}", freshnessScore);
            log.debug("ReviewHealthScorer - Documentation Score: {}", documentationScore);
            log.debug("ReviewHealthScorer - Final Review Health Score: {}", clampedScore);
        }

        return clampedScore;
    }

    private int calculateParticipationScore(List<ReviewDto> reviews) {
        double ratio = (double) reviews.size() / IDEAL_TOTAL_REVIEWS;
        return clamp((int) Math.round(ratio * 100));
    }

    private int calculateApprovalScore(List<ReviewDto> reviews) {
        long approved = 0;
        long completed = 0;

        for (ReviewDto review : reviews) {
            String state = review.state();
            if (state == null) continue;
            
            if ("APPROVED".equalsIgnoreCase(state) || "CHANGES_REQUESTED".equalsIgnoreCase(state) || "DISMISSED".equalsIgnoreCase(state)) {
                completed++;
                if ("APPROVED".equalsIgnoreCase(state)) {
                    approved++;
                }
            }
        }

        if (completed == 0) return 50;

        double ratio = (double) approved / completed;

        if (ratio >= IDEAL_APPROVAL_RATIO_LOWER && ratio <= IDEAL_APPROVAL_RATIO_UPPER) {
            return 100;
        } else if (ratio > IDEAL_APPROVAL_RATIO_UPPER) {
            // Approaching 100% approval rates may indicate rubber-stamping
            return 85; 
        } else {
            // Below ideal, scales down linearly
            return clamp((int) Math.round((ratio / IDEAL_APPROVAL_RATIO_LOWER) * 100));
        }
    }

    private int calculateChangeRequestScore(List<ReviewDto> reviews) {
        long changesRequested = reviews.stream()
                .filter(r -> "CHANGES_REQUESTED".equalsIgnoreCase(r.state()))
                .count();

        double ratio = (double) changesRequested / reviews.size();

        if (ratio == 0.0) {
            return 70; // Superficial reviews
        } else if (ratio >= IDEAL_CHANGE_REQUEST_RATIO_LOWER && ratio <= IDEAL_CHANGE_REQUEST_RATIO_UPPER) {
            return 100;
        } else if (ratio <= 0.50) {
            return 80;
        } else {
            return 50; // Extremely high percentage
        }
    }

    private int calculateCompletionScore(List<ReviewDto> reviews) {
        long incomplete = reviews.stream()
                .filter(r -> "PENDING".equalsIgnoreCase(r.state()) || "DISMISSED".equalsIgnoreCase(r.state()))
                .count();

        double incompleteRatio = (double) incomplete / reviews.size();
        return clamp((int) Math.round(100 - (incompleteRatio * 100)));
    }

    private int calculateFreshnessScore(List<ReviewDto> reviews) {
        long maxTime = 0;
        for (ReviewDto review : reviews) {
            if (review.submittedAt() != null) {
                long time = parseTime(review.submittedAt());
                if (time > maxTime) {
                    maxTime = time;
                }
            }
        }

        if (maxTime == 0) return 50;

        long daysAgo = (System.currentTimeMillis() - maxTime) / (1000L * 60 * 60 * 24);
        if (daysAgo <= 7) return 100;
        if (daysAgo <= 30) return 80;
        if (daysAgo <= 90) return 50;
        return 30;
    }

    private int calculateDocumentationScore(List<ReviewDto> reviews) {
        long meaningfulCount = reviews.stream()
                .filter(r -> r.body() != null && r.body().trim().length() >= MINIMUM_REVIEW_BODY_LENGTH)
                .count();

        double ratio = (double) meaningfulCount / reviews.size();
        return clamp((int) Math.round(ratio * 100));
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
