package com.gitinbits.analysis.repository.risk.scorer;

import com.gitinbits.dto.response.repo.CommitDto;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Evaluates commit-related engineering risk.
 * Higher score = Higher risk.
 */
@Component
public class CommitRiskScorer {
    
    public int score(List<CommitDto> commits) {
        if (commits == null || commits.isEmpty()) {
            return 100; // No commits -> Critical risk
        }

        double riskScore = 0.0;

        // 1. Recency Risk (30%)
        try {
            Instant latestCommitTime = Instant.parse(commits.get(0).timestamp());
            long daysSinceLastCommit = ChronoUnit.DAYS.between(latestCommitTime, Instant.now());
            if (daysSinceLastCommit > 180) {
                riskScore += 30; // Very stale
            } else if (daysSinceLastCommit > 90) {
                riskScore += 20;
            } else if (daysSinceLastCommit > 30) {
                riskScore += 10;
            }
        } catch (Exception ignored) {}

        // 2. Large Commits Risk (40%)
        int largeCommits = 0;
        int unverifiedCommits = 0;
        
        for (CommitDto commit : commits) {
            if ((commit.totalChanges() != null && commit.totalChanges() > 1000) || 
                (commit.filesChangedCount() != null && commit.filesChangedCount() > 50)) {
                largeCommits++;
            }
            if (Boolean.FALSE.equals(commit.verified())) {
                unverifiedCommits++;
            }
        }

        double largeCommitRatio = (double) largeCommits / commits.size();
        riskScore += (largeCommitRatio * 40.0); // Up to 40 risk points for large commits

        // 3. Unverified Commits Risk (30%)
        double unverifiedRatio = (double) unverifiedCommits / commits.size();
        riskScore += (unverifiedRatio * 30.0); // Up to 30 risk points for unverified commits

        return (int) Math.max(0, Math.min(100, Math.round(riskScore)));
    }
}
