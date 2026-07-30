package com.gitinbits.analysis.repository.risk.scorer;

import com.gitinbits.dto.response.repo.PullRequestDto;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Evaluates pull request related risk.
 * Higher score = Higher risk.
 */
@Component
public class PullRequestRiskScorer {
    public int score(List<PullRequestDto> pullRequests) {
        if (pullRequests == null || pullRequests.isEmpty()) {
            return 80; // No PRs indicates high risk of direct commits
        }

        double riskScore = 0.0;
        int openPRs = 0;
        int stalePRs = 0;
        int largePRs = 0;
        int draftPRs = 0;
        
        Instant now = Instant.now();

        for (PullRequestDto pr : pullRequests) {
            boolean isOpen = "open".equalsIgnoreCase(pr.state());
            if (isOpen) {
                openPRs++;
            }

            if (Boolean.TRUE.equals(pr.draft())) {
                draftPRs++;
            }

            if ((pr.changedFilesCount() != null && pr.changedFilesCount() > 50) || 
                (pr.commitsCount() != null && pr.commitsCount() > 30)) {
                largePRs++;
            }

            try {
                Instant createdAt = Instant.parse(pr.createdAt());
                if (isOpen && ChronoUnit.DAYS.between(createdAt, now) > 60) {
                    stalePRs++;
                }
            } catch (Exception ignored) {}
        }

        double openRatio = (double) openPRs / pullRequests.size();
        double staleRatio = openPRs > 0 ? (double) stalePRs / openPRs : 0;
        double largeRatio = (double) largePRs / pullRequests.size();
        double draftRatio = openPRs > 0 ? (double) draftPRs / openPRs : 0;

        // 1. High Open Ratio Risk (Max 25)
        riskScore += Math.min(25, openRatio * 30);
        
        // 2. Stale PR Risk (Max 35)
        riskScore += Math.min(35, staleRatio * 50);

        // 3. Large PR Risk (Max 25)
        riskScore += Math.min(25, largeRatio * 40);

        // 4. Draft Accumulation Risk (Max 15)
        riskScore += Math.min(15, draftRatio * 20);

        return (int) Math.max(0, Math.min(100, Math.round(riskScore)));
    }
}
