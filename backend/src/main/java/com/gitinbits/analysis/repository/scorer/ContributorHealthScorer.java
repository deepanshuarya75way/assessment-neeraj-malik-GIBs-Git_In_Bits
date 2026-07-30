package com.gitinbits.analysis.repository.scorer;

import com.gitinbits.dto.response.repo.ContributorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Evaluates the health of repository contributor activity.
 * Independent scorer with single responsibility: only evaluates contributors.
 */
@Component
public class ContributorHealthScorer {

    private static final Logger log = LoggerFactory.getLogger(ContributorHealthScorer.class);

    private static final int DEFAULT_EMPTY_SCORE = 50;

    // Weights
    private static final double WEIGHT_COUNT = 0.30;
    private static final double WEIGHT_DISTRIBUTION = 0.30;
    private static final double WEIGHT_BUS_FACTOR = 0.20;
    private static final double WEIGHT_ENGAGEMENT = 0.10;
    private static final double WEIGHT_DIVERSITY = 0.10;

    // Thresholds
    private static final double IDEAL_CONTRIBUTOR_COUNT = 10.0;
    private static final double IDEAL_AVERAGE_CONTRIBUTIONS = 30.0;
    private static final double IDEAL_BUS_FACTOR_RATIO = 0.30;
    private static final double POOR_BUS_FACTOR_RATIO = 0.90;

    /**
     * Calculates numeric health score (0-100) based on contributor metrics.
     *
     * @param contributors List of repository contributors
     * @return Numeric score between 0 and 100
     */
    public int score(List<ContributorDto> contributors) {
        if (contributors == null || contributors.isEmpty()) {
            return DEFAULT_EMPTY_SCORE;
        }

        long totalContributions = calculateTotalContributions(contributors);
        if (totalContributions == 0) {
            return DEFAULT_EMPTY_SCORE;
        }

        int countScore = calculateContributorCountScore(contributors);
        int distributionScore = calculateDistributionScore(contributors, totalContributions);
        int busFactorScore = calculateBusFactorScore(contributors, totalContributions);
        int engagementScore = calculateEngagementScore(contributors, totalContributions);
        int diversityScore = calculateDiversityScore(contributors, totalContributions);

        double finalScore = (countScore * WEIGHT_COUNT)
                + (distributionScore * WEIGHT_DISTRIBUTION)
                + (busFactorScore * WEIGHT_BUS_FACTOR)
                + (engagementScore * WEIGHT_ENGAGEMENT)
                + (diversityScore * WEIGHT_DIVERSITY);

        int clampedScore = clamp((int) Math.round(finalScore));

        if (log.isDebugEnabled()) {
            log.debug("ContributorHealthScorer - Contributor Count Score: {}", countScore);
            log.debug("ContributorHealthScorer - Distribution Score: {}", distributionScore);
            log.debug("ContributorHealthScorer - Bus Factor Score: {}", busFactorScore);
            log.debug("ContributorHealthScorer - Engagement Score: {}", engagementScore);
            log.debug("ContributorHealthScorer - Diversity Score: {}", diversityScore);
            log.debug("ContributorHealthScorer - Final Contributor Health Score: {}", clampedScore);
        }

        return clampedScore;
    }

    private int calculateContributorCountScore(List<ContributorDto> contributors) {
        double ratio = contributors.size() / IDEAL_CONTRIBUTOR_COUNT;
        return clamp((int) Math.round(ratio * 100));
    }

    private int calculateDistributionScore(List<ContributorDto> contributors, long totalContributions) {
        if (contributors.size() <= 1) return 20; // Poor distribution

        // Calculate Herfindahl-Hirschman Index (HHI) for concentration
        double hhi = 0;
        for (ContributorDto contributor : contributors) {
            long count = contributor.contributions() != null ? contributor.contributions() : 0;
            double share = (double) count / totalContributions;
            hhi += (share * share);
        }

        // HHI approaches 1.0 when one person dominates, and 1/N when perfectly distributed
        double score = (1.0 - hhi) * 125; // slightly boosted so 5 equal contributors can reach ~100
        return clamp((int) Math.round(score));
    }

    private int calculateBusFactorScore(List<ContributorDto> contributors, long totalContributions) {
        if (contributors.size() <= 1) return 20;

        long maxContributions = 0;
        for (ContributorDto contributor : contributors) {
            long count = contributor.contributions() != null ? contributor.contributions() : 0;
            if (count > maxContributions) {
                maxContributions = count;
            }
        }

        double ratio = (double) maxContributions / totalContributions;

        if (ratio <= IDEAL_BUS_FACTOR_RATIO) {
            return 100;
        } else if (ratio >= POOR_BUS_FACTOR_RATIO) {
            return 20;
        } else {
            // Interpolate between 100 and 20
            double penaltyRange = POOR_BUS_FACTOR_RATIO - IDEAL_BUS_FACTOR_RATIO;
            double amountOverIdeal = ratio - IDEAL_BUS_FACTOR_RATIO;
            double penaltyFraction = amountOverIdeal / penaltyRange;
            double score = 100 - (penaltyFraction * 80);
            return clamp((int) Math.round(score));
        }
    }

    private int calculateEngagementScore(List<ContributorDto> contributors, long totalContributions) {
        double averageContributions = (double) totalContributions / contributors.size();
        
        double ratio = averageContributions / IDEAL_AVERAGE_CONTRIBUTIONS;
        
        // Base score of 20 for minimal engagement
        return clamp((int) Math.round(20 + (ratio * 80)));
    }

    private int calculateDiversityScore(List<ContributorDto> contributors, long totalContributions) {
        if (contributors.size() <= 1) return 20;

        long maxContributions = 0;
        for (ContributorDto contributor : contributors) {
            long count = contributor.contributions() != null ? contributor.contributions() : 0;
            if (count > maxContributions) {
                maxContributions = count;
            }
        }

        long nonDominantContributions = totalContributions - maxContributions;
        double nonDominantRatio = (double) nonDominantContributions / totalContributions;

        // If >= 50% of contributions come from outside the largest contributor, it's highly diverse
        double score = (nonDominantRatio / 0.50) * 100;
        return clamp((int) Math.round(score));
    }

    private long calculateTotalContributions(List<ContributorDto> contributors) {
        long total = 0;
        for (ContributorDto contributor : contributors) {
            if (contributor.contributions() != null) {
                total += contributor.contributions();
            }
        }
        return total;
    }

    private int clamp(int score) {
        return Math.max(0, Math.min(100, score));
    }
}
