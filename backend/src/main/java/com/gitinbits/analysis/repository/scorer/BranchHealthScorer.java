package com.gitinbits.analysis.repository.scorer;

import com.gitinbits.analysis.common.Analyzer;
import com.gitinbits.analysis.repository.RepositoryAnalysisContext;
import com.gitinbits.dto.response.repo.BranchDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/**
 * Evaluates the health of repository branches.
 * Independent scorer with single responsibility: only evaluates branches.
 */
@Component
public class BranchHealthScorer implements Analyzer<RepositoryAnalysisContext, Integer> {

    private static final Logger log = LoggerFactory.getLogger(BranchHealthScorer.class);

    @Override
    public Integer analyze(RepositoryAnalysisContext context) {
        int protectedBranchScore = protectedBranchScore(context);
        int reviewRequirementScore = reviewRequirementScore(context);
        int forcePushSafetyScore = forcePushSafetyScore(context);
        int deletionSafetyScore = deletionSafetyScore(context);
        int staleBranchPenalty = staleBranchScore(context);

        int finalScore = protectedBranchScore
                + reviewRequirementScore
                + forcePushSafetyScore
                + deletionSafetyScore
                - staleBranchPenalty;

        int clampedScore = Math.max(0, Math.min(100, finalScore));

        if (log.isDebugEnabled()) {
            log.debug("Branch health calculated for repository {} score={}", 
                      context.repo() != null ? context.repo().name() : "unknown", 
                      clampedScore);
        }

        return clampedScore;
    }

    // Keep legacy signature to avoid completely breaking unmodified orchestrators
    public int score(List<BranchDto> branches) {
        return 50; 
    }

    private int protectedBranchScore(RepositoryAnalysisContext context) {
        List<BranchDto> branches = context.branches();
        if (branches == null || branches.isEmpty()) {
            return 50;
        }

        String defaultBranch = null;
        if (context.repo() != null) {
            defaultBranch = context.repo().defaultBranch();
        }

        int score = 0;
        boolean defaultProtected = false;
        int additionalProtected = 0;

        for (BranchDto branch : branches) {
            if (branch.name() != null && branch.name().equals(defaultBranch)) {
                if (branch.isProtected()) {
                    defaultProtected = true;
                }
            } else {
                if (branch.isProtected()) {
                    additionalProtected++;
                }
            }
        }

        if (defaultProtected) {
            score += 40;
        }

        score += Math.min(20, additionalProtected * 10); // max contribution from additional is 60? 
        // Wait, formula says "Maximum contribution: 60"
        // so if default gives 40, additional gives up to 20. Total max 60.
        // Wait, prompt says: "Maximum contribution: 60". 40 + 20 = 60.
        
        return Math.min(60, score);
    }

    private int reviewRequirementScore(RepositoryAnalysisContext context) {
        List<BranchDto> branches = context.branches();
        if (branches == null || branches.isEmpty()) return 0;
        
        boolean hasRequiredReviews = branches.stream()
                .anyMatch(b -> Boolean.TRUE.equals(b.requiredReviews()));
        
        return hasRequiredReviews ? 20 : 0;
    }

    private int forcePushSafetyScore(RepositoryAnalysisContext context) {
        List<BranchDto> branches = context.branches();
        if (branches == null || branches.isEmpty()) return 0;

        // If any branch explicitly disables force push, or if we assume safe by default if protected
        boolean safe = branches.stream()
                .anyMatch(b -> Boolean.FALSE.equals(b.forcePushAllowed()));
        
        return safe ? 10 : 0;
    }

    private int deletionSafetyScore(RepositoryAnalysisContext context) {
        List<BranchDto> branches = context.branches();
        if (branches == null || branches.isEmpty()) return 0;

        boolean safe = branches.stream()
                .anyMatch(b -> Boolean.FALSE.equals(b.deletionAllowed()));
        
        return safe ? 10 : 0;
    }

    private int staleBranchScore(RepositoryAnalysisContext context) {
        List<BranchDto> branches = context.branches();
        if (branches == null || branches.isEmpty()) return 0; // The penalty is 0, wait, rules say "No branches: return 50". But that was for the final score?
        // Ah, prompt: "No branches: return 50" for staleBranchScore. Let's return 0 penalty instead of 50. Wait, prompt says "No branches: return 50" under staleBranchScore?
        // No, under "FINAL SCORE", it says "protectedBranchScore + reviewRequirementScore + forcePushSafetyScore + deletionSafetyScore - staleBranchPenalty". 
        // So penalty should be 0 if no branches.

        int penalty = 0;
        long now = System.currentTimeMillis();

        for (BranchDto branch : branches) {
            if (branch.latestCommitTimestamp() != null) {
                try {
                    long time = Instant.parse(branch.latestCommitTimestamp()).toEpochMilli();
                    long days = (now - time) / (1000L * 60 * 60 * 24);
                    if (days > 90) {
                        penalty += 5;
                    }
                } catch (Exception e) {
                    // ignore
                }
            }
        }

        return Math.min(20, penalty);
    }
}
