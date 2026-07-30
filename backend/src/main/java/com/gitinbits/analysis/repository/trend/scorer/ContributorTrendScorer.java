package com.gitinbits.analysis.repository.trend.scorer;

import com.gitinbits.dto.response.repo.ContributorDto;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class ContributorTrendScorer {
    public int score(List<ContributorDto> contributors) {
        if (contributors == null || contributors.isEmpty()) {
            return 30; // No contributors -> Decline
        }

        if (contributors.size() == 1) {
            return 40; // Only one active contributor -> Slight Decline (bus factor)
        }

        double score = 50.0; // Start stable

        // Reward diverse contribution pools
        if (contributors.size() > 10) {
            score += 30; // Very healthy
        } else if (contributors.size() > 3) {
            score += 20; 
        } else {
            score += 10;
        }

        // Check if top contributor completely dominates
        int totalContributions = 0;
        int topContributions = 0;
        
        for (ContributorDto contributor : contributors) {
            int count = contributor.contributions() != null ? contributor.contributions() : 0;
            totalContributions += count;
            if (count > topContributions) {
                topContributions = count;
            }
        }

        if (totalContributions > 0) {
            double topRatio = (double) topContributions / totalContributions;
            if (topRatio > 0.9) {
                // Highly centralized
                score -= 20;
            } else if (topRatio < 0.5) {
                // Highly distributed
                score += 20;
            }
        }

        return (int) Math.max(0, Math.min(100, Math.round(score)));
    }
}
