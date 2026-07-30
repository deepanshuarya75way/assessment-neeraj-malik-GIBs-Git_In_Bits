package com.gitinbits.analysis.repository.risk.scorer;

import com.gitinbits.dto.response.repo.BranchDto;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * Evaluates branch management risk.
 * Higher score = Higher risk.
 */
@Component
public class BranchRiskScorer {
    public int score(List<BranchDto> branches) {
        if (branches == null || branches.isEmpty()) {
            return 100; // Complete lack of branch metadata -> Max Risk
        }

        double riskScore = 0.0;
        int defaultBranchIndex = -1;
        
        for (int i = 0; i < branches.size(); i++) {
            if ("main".equals(branches.get(i).name()) || "master".equals(branches.get(i).name())) {
                defaultBranchIndex = i;
                break;
            }
        }

        if (defaultBranchIndex != -1) {
            BranchDto defaultBranch = branches.get(defaultBranchIndex);
            if (!defaultBranch.isProtected()) {
                riskScore += 80; // Huge risk if default branch is unprotected
            } else {
                if (Boolean.TRUE.equals(defaultBranch.forcePushAllowed())) riskScore += 20;
                if (Boolean.TRUE.equals(defaultBranch.deletionAllowed())) riskScore += 20;
                if (Boolean.FALSE.equals(defaultBranch.requiredReviews())) riskScore += 30;
                if (Boolean.FALSE.equals(defaultBranch.requiredStatusChecks())) riskScore += 20;
            }
        } else {
            // Cannot find default branch, assume generic risk
            riskScore += 50; 
        }

        return (int) Math.max(0, Math.min(100, Math.round(riskScore)));
    }
}
