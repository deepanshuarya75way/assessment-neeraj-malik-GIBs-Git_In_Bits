package com.gitinbits.analysis.repository.stability.scorer;

import com.gitinbits.dto.response.repo.CommitDto;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class CommitStabilityScorer {

    private static final int MAX_IDEAL_COMMIT_SIZE = 500;
    
    public int score(List<CommitDto> commits) {
        if (commits == null || commits.isEmpty()) {
            return 0; // Penalize lack of activity
        }

        List<CommitDto> sortedCommits = new ArrayList<>(commits);
        sortedCommits.sort(Comparator.comparing(c -> {
            try {
                return Instant.parse(c.timestamp());
            } catch (Exception e) {
                return Instant.EPOCH;
            }
        }));

        double regularActivityScore = calculateRegularActivity(sortedCommits);
        double sizeConsistencyScore = calculateSizeConsistency(commits);
        double recentActivityScore = calculateRecentActivity(sortedCommits);

        double total = (regularActivityScore * 0.40) + (sizeConsistencyScore * 0.35) + (recentActivityScore * 0.25);
        return (int) Math.max(0, Math.min(100, Math.round(total)));
    }

    private double calculateRegularActivity(List<CommitDto> sortedCommits) {
        if (sortedCommits.size() < 2) return 50.0;
        
        long maxGapDays = 0;
        long totalGapDays = 0;
        int gaps = 0;

        for (int i = 1; i < sortedCommits.size(); i++) {
            try {
                Instant prev = Instant.parse(sortedCommits.get(i - 1).timestamp());
                Instant curr = Instant.parse(sortedCommits.get(i).timestamp());
                long gap = ChronoUnit.DAYS.between(prev, curr);
                if (gap >= 0) {
                    if (gap > maxGapDays) maxGapDays = gap;
                    totalGapDays += gap;
                    gaps++;
                }
            } catch (Exception ignored) {}
        }

        if (gaps == 0) return 50.0;

        long avgGap = totalGapDays / gaps;
        
        double score = 100.0;
        if (avgGap > 7) score -= (avgGap - 7) * 2;
        if (maxGapDays > 30) score -= (maxGapDays - 30);
        
        return Math.max(0, Math.min(100, score));
    }

    private double calculateSizeConsistency(List<CommitDto> commits) {
        int consistentCommits = 0;
        int validCommits = 0;

        for (CommitDto commit : commits) {
            int totalChanges = commit.totalChanges() != null ? commit.totalChanges() : 
                               ((commit.additions() != null ? commit.additions() : 0) + 
                                (commit.deletions() != null ? commit.deletions() : 0));
            if (totalChanges == 0) continue;
            validCommits++;

            if (totalChanges <= MAX_IDEAL_COMMIT_SIZE) {
                consistentCommits++;
            }
        }
        
        if (validCommits == 0) return 50.0;
        
        double consistencyRatio = (double) consistentCommits / validCommits;
        return consistencyRatio * 100.0;
    }

    private double calculateRecentActivity(List<CommitDto> sortedCommits) {
        try {
            CommitDto latest = sortedCommits.get(sortedCommits.size() - 1);
            Instant lastCommitTime = Instant.parse(latest.timestamp());
            long daysSinceLast = ChronoUnit.DAYS.between(lastCommitTime, Instant.now());

            if (daysSinceLast <= 7) return 100.0;
            if (daysSinceLast <= 30) return 80.0;
            if (daysSinceLast <= 90) return 50.0;
            if (daysSinceLast <= 180) return 20.0;
            return 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }
}
