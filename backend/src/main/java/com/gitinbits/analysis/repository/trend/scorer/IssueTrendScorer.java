package com.gitinbits.analysis.repository.trend.scorer;

import com.gitinbits.dto.response.repo.IssueDto;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class IssueTrendScorer {
    public int score(List<IssueDto> issues) {
        if (issues == null || issues.isEmpty()) {
            return 50; // Neutral if unused
        }

        int recentOpened = 0;
        int recentClosed = 0;
        int totalOpen = 0;
        int staleOpen = 0;

        Instant now = Instant.now();

        for (IssueDto issue : issues) {
            boolean isOpen = "open".equalsIgnoreCase(issue.state());
            if (isOpen) {
                totalOpen++;
            }

            try {
                if (issue.closedAt() != null && !isOpen) {
                    Instant closed = Instant.parse(issue.closedAt());
                    if (ChronoUnit.DAYS.between(closed, now) <= 30) {
                        recentClosed++;
                    }
                }

                if (issue.createdAt() != null) {
                    Instant created = Instant.parse(issue.createdAt());
                    long daysSinceCreated = ChronoUnit.DAYS.between(created, now);
                    
                    if (daysSinceCreated <= 30) {
                        recentOpened++;
                    }
                    if (isOpen && daysSinceCreated > 90) {
                        staleOpen++;
                    }
                }
            } catch (Exception ignored) {}
        }

        double score = 50.0;

        // Reward closing issues faster than opening
        if (recentClosed > recentOpened && recentClosed > 0) {
            score += 25;
        } else if (recentClosed > 0) {
            score += 15;
        } else if (recentOpened > 0) {
            score -= 10;
        }

        // Penalize stale unresolved issues
        if (totalOpen > 0) {
            double staleRatio = (double) staleOpen / totalOpen;
            if (staleRatio > 0.5) {
                score -= 20;
            } else if (staleRatio > 0.2) {
                score -= 10;
            }
        }

        return (int) Math.max(0, Math.min(100, Math.round(score)));
    }
}
