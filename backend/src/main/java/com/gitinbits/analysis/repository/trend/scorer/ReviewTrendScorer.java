package com.gitinbits.analysis.repository.trend.scorer;

import com.gitinbits.dto.response.repo.ReviewDto;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class ReviewTrendScorer {
    public int score(List<ReviewDto> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return 30; // Lack of reviews indicates declining/poor discipline over time
        }

        int recentReviews = 0;
        int olderReviews = 0;
        Instant now = Instant.now();

        for (ReviewDto review : reviews) {
            try {
                if (review.submittedAt() != null) {
                    Instant submitted = Instant.parse(review.submittedAt());
                    long days = ChronoUnit.DAYS.between(submitted, now);
                    if (days <= 30) {
                        recentReviews++;
                    } else if (days <= 90) {
                        olderReviews++;
                    }
                }
            } catch (Exception ignored) {}
        }

        if (recentReviews == 0 && olderReviews == 0) {
            return 30; // Stale reviews -> Decline
        }

        if (olderReviews == 0) {
            return recentReviews > 5 ? 85 : 70; // Newly established review process
        }

        double ratio = (double) recentReviews / (olderReviews / 2.0);

        if (ratio >= 1.5) return 90;      // Strong Improvement
        if (ratio >= 1.1) return 80;      // Improving
        if (ratio >= 0.8) return 60;      // Stable
        if (ratio >= 0.5) return 40;      // Slight Decline
        return 20;                        // Strong Decline
    }
}
