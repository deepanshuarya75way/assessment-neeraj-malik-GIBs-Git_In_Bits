package com.gitinbits.analysis.repository.trend.scorer;

import com.gitinbits.dto.response.repo.ReleaseDto;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class ReleaseTrendScorer {
    public int score(List<ReleaseDto> releases) {
        if (releases == null || releases.isEmpty()) {
            return 50; // "Repositories with no releases should receive a neutral score"
        }

        int recentReleases = 0;
        int olderReleases = 0;
        Instant now = Instant.now();

        for (ReleaseDto release : releases) {
            try {
                if (release.publishedAt() != null) {
                    Instant published = Instant.parse(release.publishedAt());
                    long days = ChronoUnit.DAYS.between(published, now);
                    if (days <= 90) {
                        recentReleases++;
                    } else if (days <= 180) {
                        olderReleases++;
                    }
                }
            } catch (Exception ignored) {}
        }

        if (recentReleases == 0 && olderReleases == 0) {
            return 30; // Releases have completely stagnated
        }

        if (olderReleases == 0) {
            return recentReleases > 1 ? 85 : 70; // Newly established release cadence
        }

        double ratio = (double) recentReleases / olderReleases;

        if (ratio >= 1.5) return 90;      // Strong Improvement
        if (ratio >= 1.0) return 80;      // Improving (maintaining or growing pace)
        if (ratio >= 0.5) return 60;      // Stable
        return 40;                        // Slight Decline
    }
}
