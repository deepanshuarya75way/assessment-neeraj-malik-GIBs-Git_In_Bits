package com.gitinbits.analysis.repository.stability.scorer;

import com.gitinbits.dto.response.repo.ReleaseDto;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class ReleaseStabilityScorer {

    public int score(List<ReleaseDto> releases) {
        if (releases == null || releases.isEmpty()) {
            return 0; // Penalize lack of releases
        }

        List<ReleaseDto> sorted = new ArrayList<>(releases);
        sorted.sort(Comparator.comparing(r -> {
            try {
                return Instant.parse(r.publishedAt());
            } catch (Exception e) {
                return Instant.EPOCH;
            }
        }));

        double recentScore = calculateRecentActivity(sorted);
        double frequencyScore = calculateFrequency(sorted);

        double total = (recentScore * 0.5) + (frequencyScore * 0.5);
        return (int) Math.max(0, Math.min(100, Math.round(total)));
    }

    private double calculateRecentActivity(List<ReleaseDto> sorted) {
        try {
            ReleaseDto latest = sorted.get(sorted.size() - 1);
            Instant lastReleaseTime = Instant.parse(latest.publishedAt());
            long daysSinceLast = ChronoUnit.DAYS.between(lastReleaseTime, Instant.now());

            if (daysSinceLast <= 30) return 100.0;
            if (daysSinceLast <= 90) return 80.0;
            if (daysSinceLast <= 180) return 50.0;
            if (daysSinceLast <= 365) return 20.0;
            return 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    private double calculateFrequency(List<ReleaseDto> sorted) {
        if (sorted.size() < 2) return 50.0;

        long maxGapDays = 0;
        long totalGapDays = 0;
        int gaps = 0;

        for (int i = 1; i < sorted.size(); i++) {
            try {
                Instant prev = Instant.parse(sorted.get(i - 1).publishedAt());
                Instant curr = Instant.parse(sorted.get(i).publishedAt());
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
        if (avgGap > 90) score -= (avgGap - 90) / 2.0; 
        if (maxGapDays > 180) score -= (maxGapDays - 180) / 4.0; 

        return Math.max(0, Math.min(100, score));
    }
}
