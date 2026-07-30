package com.gitinbits.analysis.repository.scorer;

import com.gitinbits.dto.response.repo.IssueDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/**
 * Evaluates the health of repository issues.
 * Independent scorer with single responsibility: only evaluates issues.
 */
@Component
public class IssueHealthScorer {

    private static final Logger log = LoggerFactory.getLogger(IssueHealthScorer.class);

    private static final int DEFAULT_EMPTY_SCORE = 50;
    
    // Weights
    private static final double WEIGHT_RESOLUTION = 0.30;
    private static final double WEIGHT_BACKLOG = 0.20;
    private static final double WEIGHT_FRESHNESS = 0.20;
    private static final double WEIGHT_ORGANIZATION = 0.15;
    private static final double WEIGHT_ASSIGNMENT = 0.10;
    private static final double WEIGHT_DISCUSSION = 0.05;

    // Thresholds
    private static final int BACKLOG_SMALL_REPO_THRESHOLD = 10;
    private static final double IDEAL_COMMENTS_LOWER = 1.0;
    private static final double IDEAL_COMMENTS_UPPER = 8.0;
    private static final double PENALTY_COMMENTS_THRESHOLD = 25.0;

    /**
     * Calculates numeric health score (0-100) based on issue resolution, freshness, and organization.
     *
     * @param issues List of repository issues
     * @return Numeric score between 0 and 100
     */
    public int score(List<IssueDto> issues) {
        if (issues == null || issues.isEmpty()) {
            return DEFAULT_EMPTY_SCORE;
        }

        int resolutionScore = calculateResolutionScore(issues);
        int backlogScore = calculateBacklogScore(issues);
        int freshnessScore = calculateFreshnessScore(issues);
        int organizationScore = calculateOrganizationScore(issues);
        int assignmentScore = calculateAssignmentScore(issues);
        int discussionScore = calculateDiscussionScore(issues);

        double finalScore = (resolutionScore * WEIGHT_RESOLUTION)
                + (backlogScore * WEIGHT_BACKLOG)
                + (freshnessScore * WEIGHT_FRESHNESS)
                + (organizationScore * WEIGHT_ORGANIZATION)
                + (assignmentScore * WEIGHT_ASSIGNMENT)
                + (discussionScore * WEIGHT_DISCUSSION);

        int clampedScore = clamp((int) Math.round(finalScore));

        if (log.isDebugEnabled()) {
            log.debug("IssueHealthScorer - Resolution Score: {}", resolutionScore);
            log.debug("IssueHealthScorer - Backlog Score: {}", backlogScore);
            log.debug("IssueHealthScorer - Freshness Score: {}", freshnessScore);
            log.debug("IssueHealthScorer - Organization Score: {}", organizationScore);
            log.debug("IssueHealthScorer - Assignment Score: {}", assignmentScore);
            log.debug("IssueHealthScorer - Discussion Score: {}", discussionScore);
            log.debug("IssueHealthScorer - Final Issue Health Score: {}", clampedScore);
        }

        return clampedScore;
    }

    private int calculateResolutionScore(List<IssueDto> issues) {
        long closed = issues.stream().filter(i -> "closed".equalsIgnoreCase(i.state())).count();
        double ratio = (double) closed / issues.size();
        return clamp((int) Math.round(ratio * 100));
    }

    private int calculateBacklogScore(List<IssueDto> issues) {
        long open = issues.stream().filter(i -> "open".equalsIgnoreCase(i.state())).count();
        double openRatio = (double) open / issues.size();
        
        // Small repos penalty dampening
        double penaltyFactor = 1.0;
        if (open < BACKLOG_SMALL_REPO_THRESHOLD) {
            penaltyFactor = (double) open / BACKLOG_SMALL_REPO_THRESHOLD;
        }
        
        double penalty = (openRatio * 100) * penaltyFactor;
        return clamp((int) Math.round(100 - penalty));
    }

    private int calculateFreshnessScore(List<IssueDto> issues) {
        long totalScore = 0;
        int validCount = 0;
        long now = System.currentTimeMillis();

        for (IssueDto issue : issues) {
            boolean isOpen = "open".equalsIgnoreCase(issue.state());
            long time = 0;

            if (isOpen && issue.createdAt() != null) {
                time = parseTime(issue.createdAt());
            } else if (!isOpen && issue.closedAt() != null) {
                time = parseTime(issue.closedAt());
            } else if (issue.updatedAt() != null) {
                time = parseTime(issue.updatedAt());
            }

            if (time == 0) continue;

            long daysAgo = (now - time) / (1000L * 60 * 60 * 24);
            int score;
            
            if (isOpen) {
                if (daysAgo <= 30) score = 100;
                else if (daysAgo <= 90) score = 80;
                else if (daysAgo <= 180) score = 50;
                else score = 20;
            } else {
                if (daysAgo <= 30) score = 100;
                else if (daysAgo <= 90) score = 80;
                else score = 60; // old closed issues aren't as bad as old open issues
            }
            
            totalScore += score;
            validCount++;
        }

        if (validCount == 0) return 50;
        return clamp((int) Math.round((double) totalScore / validCount));
    }

    private int calculateOrganizationScore(List<IssueDto> issues) {
        long totalScore = 0;
        for (IssueDto issue : issues) {
            int score = 0;
            if (issue.labels() != null && !issue.labels().isEmpty()) {
                score += 80;
            }
            if (issue.milestone() != null && !issue.milestone().trim().isEmpty()) {
                score += 20;
            }
            totalScore += score;
        }
        return clamp((int) Math.round((double) totalScore / issues.size()));
    }

    private int calculateAssignmentScore(List<IssueDto> issues) {
        long assigned = issues.stream()
                .filter(i -> i.assignees() != null && !i.assignees().isEmpty())
                .count();
        double ratio = (double) assigned / issues.size();
        
        // Baseline 50 to not over-penalize small teams ignoring assignments
        return clamp((int) Math.round(50 + (ratio * 50))); 
    }

    private int calculateDiscussionScore(List<IssueDto> issues) {
        long totalComments = 0;
        for (IssueDto issue : issues) {
            if (issue.commentCount() != null) {
                totalComments += issue.commentCount();
            }
        }
        double avgComments = (double) totalComments / issues.size();

        if (avgComments >= IDEAL_COMMENTS_LOWER && avgComments <= IDEAL_COMMENTS_UPPER) {
            return 100;
        } else if (avgComments < IDEAL_COMMENTS_LOWER) {
            // Scales from 40 up to 100
            return clamp((int) Math.round((avgComments / IDEAL_COMMENTS_LOWER) * 60 + 40)); 
        } else {
            if (avgComments >= PENALTY_COMMENTS_THRESHOLD) {
                return 40;
            }
            double ratio = (avgComments - IDEAL_COMMENTS_UPPER) / (PENALTY_COMMENTS_THRESHOLD - IDEAL_COMMENTS_UPPER);
            return clamp((int) Math.round(100 - (ratio * 60))); // Drops from 100 to 40
        }
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
