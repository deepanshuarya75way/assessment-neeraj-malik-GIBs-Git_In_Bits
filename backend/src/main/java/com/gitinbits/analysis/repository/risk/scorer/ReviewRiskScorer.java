package com.gitinbits.analysis.repository.risk.scorer;

import com.gitinbits.dto.response.repo.ReviewDto;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * Evaluates code review related risk.
 * Higher score = Higher risk.
 */
@Component
public class ReviewRiskScorer {
    public int score(List<ReviewDto> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return 100; // Missing reviews entirely increases risk to max
        }

        double riskScore = 0.0;
        int approved = 0;
        int changesRequested = 0;

        for (ReviewDto review : reviews) {
            if ("APPROVED".equalsIgnoreCase(review.state())) {
                approved++;
            } else if ("CHANGES_REQUESTED".equalsIgnoreCase(review.state())) {
                changesRequested++;
            }
        }

        double approvalRate = (double) approved / reviews.size();
        double rejectionRate = (double) changesRequested / reviews.size();

        // 1. Low Approval Rate Risk (Max 60)
        // If approval rate is 1.0 -> 0 risk. If 0.0 -> 60 risk.
        riskScore += (1.0 - approvalRate) * 60.0;

        // 2. High Rejection Volatility Risk (Max 40)
        riskScore += Math.min(40, rejectionRate * 60.0);

        return (int) Math.max(0, Math.min(100, Math.round(riskScore)));
    }
}
