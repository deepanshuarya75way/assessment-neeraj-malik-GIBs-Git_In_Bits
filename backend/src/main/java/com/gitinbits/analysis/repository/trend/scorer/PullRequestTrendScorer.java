package com.gitinbits.analysis.repository.trend.scorer;

import com.gitinbits.dto.response.repo.PullRequestDto;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class PullRequestTrendScorer {
    public int score(List<PullRequestDto> pullRequests) {
        if (pullRequests == null || pullRequests.isEmpty()) {
            return 50; // Neutral if unused
        }

        int recentMerged = 0;
        int recentOpened = 0;
        int draftPRs = 0;
        int staleOpen = 0;

        Instant now = Instant.now();

        for (PullRequestDto pr : pullRequests) {
            boolean isOpen = "open".equalsIgnoreCase(pr.state());
            
            if (Boolean.TRUE.equals(pr.draft()) && isOpen) {
                draftPRs++;
            }

            try {
                if (pr.mergedAt() != null) {
                    Instant merged = Instant.parse(pr.mergedAt());
                    if (ChronoUnit.DAYS.between(merged, now) <= 30) {
                        recentMerged++;
                    }
                }
                
                if (isOpen) {
                    Instant created = Instant.parse(pr.createdAt());
                    long daysOpen = ChronoUnit.DAYS.between(created, now);
                    if (daysOpen <= 30) {
                        recentOpened++;
                    } else if (daysOpen > 60) {
                        staleOpen++;
                    }
                }
            } catch (Exception ignored) {}
        }

        double score = 50.0; // Start stable

        // Reward merges
        if (recentMerged > recentOpened && recentMerged > 0) {
            score += 25; 
        } else if (recentMerged > 0) {
            score += 15;
        } else if (recentOpened > 0) {
            // Opening but not merging
            score -= 10;
        }

        // Penalize stale/draft accumulation
        double staleRatio = (double) staleOpen / pullRequests.size();
        if (staleRatio > 0.3) {
            score -= 20;
        } else if (staleRatio > 0.1) {
            score -= 10;
        }

        double draftRatio = (double) draftPRs / pullRequests.size();
        if (draftRatio > 0.2) {
            score -= 15;
        }

        return (int) Math.max(0, Math.min(100, Math.round(score)));
    }
}
