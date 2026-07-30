package com.gitinbits.analysis.repository.trend.scorer;

import com.gitinbits.dto.response.repo.CommitDto;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class CommitTrendScorer {
    public int score(List<CommitDto> commits) {
        if (commits == null || commits.isEmpty()) {
            return 30; // Complete inactivity -> Strong Decline
        }

        int recentCommits = 0;
        int olderCommits = 0;
        Instant now = Instant.now();

        for (CommitDto commit : commits) {
            try {
                Instant time = Instant.parse(commit.timestamp());
                long days = ChronoUnit.DAYS.between(time, now);
                if (days <= 30) {
                    recentCommits++;
                } else if (days <= 90) {
                    olderCommits++;
                }
            } catch (Exception ignored) {}
        }

        if (recentCommits == 0 && olderCommits == 0) {
            return 30; // Stale repository -> Strong Decline
        }

        if (olderCommits == 0) {
            // New repository or newly active
            return recentCommits > 5 ? 85 : 70;
        }

        double ratio = (double) recentCommits / (olderCommits / 2.0); // older is a 60-day window, recent is 30-day

        if (ratio >= 1.5) return 90;      // Strong Improvement
        if (ratio >= 1.1) return 80;      // Improving
        if (ratio >= 0.8) return 60;      // Stable
        if (ratio >= 0.5) return 40;      // Slight Decline
        return 20;                        // Strong Decline
    }
}
