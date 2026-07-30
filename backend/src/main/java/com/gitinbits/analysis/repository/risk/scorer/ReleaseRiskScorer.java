package com.gitinbits.analysis.repository.risk.scorer;

import com.gitinbits.dto.response.repo.ReleaseDto;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Evaluates release management risk.
 * Higher score = Higher risk.
 */
@Component
public class ReleaseRiskScorer {
    public int score(List<ReleaseDto> releases) {
        if (releases == null || releases.isEmpty()) {
            return 80; // High risk if there are no formalized releases
        }

        double riskScore = 0.0;
        int prereleases = 0;
        int draftReleases = 0;
        
        Instant now = Instant.now();
        Instant latestStableTime = null;

        for (ReleaseDto release : releases) {
            if (release.prerelease()) {
                prereleases++;
            } else if (release.draft()) {
                draftReleases++;
            } else {
                try {
                    Instant time = Instant.parse(release.publishedAt());
                    if (latestStableTime == null || time.isAfter(latestStableTime)) {
                        latestStableTime = time;
                    }
                } catch (Exception ignored) {}
            }
        }

        double prereleaseRatio = (double) prereleases / releases.size();
        double draftRatio = (double) draftReleases / releases.size();

        // 1. High Prerelease Ratio Risk (Max 20)
        riskScore += Math.min(20, prereleaseRatio * 30);
        
        // 2. Draft Accumulation Risk (Max 20)
        riskScore += Math.min(20, draftRatio * 30);

        // 3. Stale Release Risk (Max 60)
        if (latestStableTime != null) {
            long daysSinceLast = ChronoUnit.DAYS.between(latestStableTime, now);
            if (daysSinceLast > 365) {
                riskScore += 60; // Very stale
            } else if (daysSinceLast > 180) {
                riskScore += 40;
            } else if (daysSinceLast > 90) {
                riskScore += 20;
            }
        } else {
            // No stable releases, only prerelease/drafts
            riskScore += 60;
        }

        return (int) Math.max(0, Math.min(100, Math.round(riskScore)));
    }
}
