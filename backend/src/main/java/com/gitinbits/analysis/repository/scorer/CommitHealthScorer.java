package com.gitinbits.analysis.repository.scorer;

import com.gitinbits.dto.response.repo.CommitDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Evaluates the health of repository commit history.
 * Independent scorer with single responsibility: only evaluates commits.
 */
@Component
public class CommitHealthScorer {

    private static final int EMPTY_REPO_SCORE = 30;

    // Metric Weights
    private static final double WEIGHT_FREQUENCY = 0.30;
    private static final double WEIGHT_SIZE = 0.25;
    private static final double WEIGHT_FILES_CHANGED = 0.15;
    private static final double WEIGHT_VERIFIED = 0.15;
    private static final double WEIGHT_CONTRIBUTORS = 0.15;

    // Normalization Targets & Thresholds
    private static final int TARGET_COMMIT_COUNT = 50;
    private static final double MAX_IDEAL_COMMIT_SIZE = 500.0;
    private static final double MAX_IDEAL_FILES_CHANGED = 10.0;
    private static final int TARGET_CONTRIBUTOR_COUNT = 5;

    private static final int MAX_SCORE = 100;
    private static final int MIN_SCORE = 0;

    /**
     * Calculates numeric health score (0-100) based on weighted commit metrics.
     *
     * @param commits List of repository commits
     * @return Numeric score between 0 and 100
     */
    public int score(List<CommitDto> commits) {
        if (commits == null || commits.isEmpty()) {
            return EMPTY_REPO_SCORE;
        }

        int freqScore = calculateFrequencyScore(commits);
        int sizeScore = calculateSizeScore(commits);
        int fileScore = calculateFilesChangedScore(commits);
        int verifiedScore = calculateVerifiedScore(commits);
        int contributorScore = calculateContributorScore(commits);

        double weightedScore = (freqScore * WEIGHT_FREQUENCY)
                + (sizeScore * WEIGHT_SIZE)
                + (fileScore * WEIGHT_FILES_CHANGED)
                + (verifiedScore * WEIGHT_VERIFIED)
                + (contributorScore * WEIGHT_CONTRIBUTORS);

        return clamp((int) Math.round(weightedScore));
    }

    private int calculateFrequencyScore(List<CommitDto> commits) {
        double ratio = (double) commits.size() / TARGET_COMMIT_COUNT;
        return clamp((int) Math.round(ratio * 100));
    }

    private int calculateSizeScore(List<CommitDto> commits) {
        long totalSize = 0;
        for (CommitDto commit : commits) {
            if (commit.totalChanges() != null) {
                totalSize += commit.totalChanges();
            } else {
                int add = commit.additions() != null ? commit.additions() : 0;
                int del = commit.deletions() != null ? commit.deletions() : 0;
                totalSize += (add + del);
            }
        }
        double avgSize = (double) totalSize / commits.size();
        
        // Smaller commits are better; size above MAX_IDEAL_COMMIT_SIZE penalizes the score
        double penaltyRatio = avgSize / MAX_IDEAL_COMMIT_SIZE;
        int score = 100 - (int) Math.round(penaltyRatio * 100);
        return clamp(score);
    }

    private int calculateFilesChangedScore(List<CommitDto> commits) {
        long totalFiles = 0;
        for (CommitDto commit : commits) {
            if (commit.filesChangedCount() != null) {
                totalFiles += commit.filesChangedCount();
            }
        }
        double avgFiles = (double) totalFiles / commits.size();
        
        // Fewer files changed per commit is better
        double penaltyRatio = avgFiles / MAX_IDEAL_FILES_CHANGED;
        int score = 100 - (int) Math.round(penaltyRatio * 100);
        return clamp(score);
    }

    private int calculateVerifiedScore(List<CommitDto> commits) {
        long verifiedCount = commits.stream()
                .filter(c -> Boolean.TRUE.equals(c.verified()))
                .count();
        double ratio = (double) verifiedCount / commits.size();
        return clamp((int) Math.round(ratio * 100));
    }

    private int calculateContributorScore(List<CommitDto> commits) {
        Set<String> uniqueAuthors = commits.stream()
                .map(CommitDto::authorName)
                .filter(name -> name != null && !name.trim().isEmpty())
                .collect(Collectors.toSet());
                
        double ratio = (double) uniqueAuthors.size() / TARGET_CONTRIBUTOR_COUNT;
        return clamp((int) Math.round(ratio * 100));
    }

    private int clamp(int score) {
        return Math.max(MIN_SCORE, Math.min(MAX_SCORE, score));
    }
}
