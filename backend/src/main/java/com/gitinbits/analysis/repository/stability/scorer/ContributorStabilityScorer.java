package com.gitinbits.analysis.repository.stability.scorer;

import com.gitinbits.dto.response.repo.ContributorDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ContributorStabilityScorer {

    public int score(List<ContributorDto> contributors) {
        if (contributors == null || contributors.isEmpty()) {
            return 0;
        }

        int totalContributions = 0;
        int maxContributions = 0;

        for (ContributorDto contributor : contributors) {
            int count = contributor.contributions() != null ? contributor.contributions() : 0;
            totalContributions += count;
            if (count > maxContributions) {
                maxContributions = count;
            }
        }

        if (totalContributions == 0) {
            return 0;
        }

        double concentration = (double) maxContributions / totalContributions;

        double score;
        if (concentration <= 0.15) {
            score = 100.0;
        } else if (concentration <= 0.35) {
            score = 100.0 - ((concentration - 0.15) * 100.0);
        } else if (concentration <= 0.80) {
            score = 80.0 - ((concentration - 0.35) * (40.0 / 0.45));
        } else {
            score = 40.0 - ((concentration - 0.80) * (25.0 / 0.20));
        }

        return (int) Math.max(0, Math.min(100, Math.round(score)));
    }
}
