package com.gitinbits.analysis.repository.stability.scorer;

import com.gitinbits.dto.response.repo.BranchDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BranchStabilityScorer {

    public int score(List<BranchDto> branches) {
        if (branches == null || branches.isEmpty()) {
            return 0; // Penalize lack of data
        }

        double totalScore = 0;
        for (BranchDto branch : branches) {
            totalScore += scoreBranch(branch);
        }

        double avgScore = totalScore / branches.size();
        return (int) Math.max(0, Math.min(100, Math.round(avgScore)));
    }

    private double scoreBranch(BranchDto branch) {
        if (!branch.isProtected()) {
            return 10.0; // Unprotected branch score penalty
        }
        
        double score = 60.0; // Base score for protected branches
        
        if (Boolean.TRUE.equals(branch.requiredReviews())) score += 20.0;
        if (Boolean.TRUE.equals(branch.requiredStatusChecks())) score += 20.0;
        
        if (Boolean.TRUE.equals(branch.forcePushAllowed())) score -= 30.0;
        if (Boolean.TRUE.equals(branch.deletionAllowed())) score -= 30.0;
        
        return Math.max(0, Math.min(100, score));
    }
}
