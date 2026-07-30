package com.gitinbits.analysis.repository.stability.scorer;

import com.gitinbits.dto.response.repo.DeploymentDto;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class DeploymentStabilityScorer {

    public int score(List<DeploymentDto> deployments) {
        if (deployments == null || deployments.isEmpty()) {
            return 0; // Penalize no deployments
        }

        List<DeploymentDto> sorted = new ArrayList<>(deployments);
        sorted.sort(Comparator.comparing(d -> {
            try {
                return Instant.parse(d.createdAt());
            } catch (Exception e) {
                return Instant.EPOCH;
            }
        }));

        double recentScore = calculateRecentActivity(sorted);
        double frequencyScore = calculateFrequency(sorted);

        double total = (recentScore * 0.6) + (frequencyScore * 0.4);
        return (int) Math.max(0, Math.min(100, Math.round(total)));
    }

    private double calculateRecentActivity(List<DeploymentDto> sorted) {
        try {
            DeploymentDto latest = sorted.get(sorted.size() - 1);
            Instant lastTime = Instant.parse(latest.createdAt());
            long daysSinceLast = ChronoUnit.DAYS.between(lastTime, Instant.now());

            if (daysSinceLast <= 7) return 100.0;
            if (daysSinceLast <= 30) return 80.0;
            if (daysSinceLast <= 90) return 50.0;
            if (daysSinceLast <= 180) return 20.0;
            return 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    private double calculateFrequency(List<DeploymentDto> sorted) {
        if (sorted.size() < 2) return 50.0;
        
        long maxGapDays = 0;
        long totalGapDays = 0;
        int gaps = 0;

        for (int i = 1; i < sorted.size(); i++) {
            try {
                Instant prev = Instant.parse(sorted.get(i - 1).createdAt());
                Instant curr = Instant.parse(sorted.get(i).createdAt());
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
        if (avgGap > 30) score -= (avgGap - 30);
        
        return Math.max(0, Math.min(100, score));
    }
}
