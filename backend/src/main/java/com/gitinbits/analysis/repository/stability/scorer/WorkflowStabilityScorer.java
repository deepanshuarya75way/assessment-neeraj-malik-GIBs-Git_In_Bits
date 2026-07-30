package com.gitinbits.analysis.repository.stability.scorer;

import com.gitinbits.dto.response.repo.WorkflowRunDto;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class WorkflowStabilityScorer {

    public int score(List<WorkflowRunDto> workflows) {
        if (workflows == null || workflows.isEmpty()) {
            return 0; // Penalize lack of pipelines
        }

        int totalRuns = workflows.size();
        int successfulRuns = 0;
        long maxRecentBonus = 0;

        for (WorkflowRunDto run : workflows) {
            if ("success".equalsIgnoreCase(run.conclusion())) {
                successfulRuns++;
            }

            try {
                Instant runTime = Instant.parse(run.createdAt());
                long daysAgo = ChronoUnit.DAYS.between(runTime, Instant.now());
                if (daysAgo <= 7) {
                    maxRecentBonus = 10;
                } else if (daysAgo <= 30 && maxRecentBonus < 5) {
                    maxRecentBonus = 5;
                }
            } catch (Exception ignored) {}
        }

        double ratio = (double) successfulRuns / totalRuns;
        double baseScore = ratio * 90.0; // Up to 90 for total success ratio
        
        double total = baseScore + maxRecentBonus; // Bonus pushes up to 100
        return (int) Math.max(0, Math.min(100, Math.round(total)));
    }
}
