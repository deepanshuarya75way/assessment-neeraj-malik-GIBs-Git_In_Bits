package com.gitinbits.analysis.repository.finding;

import com.gitinbits.analysis.repository.RepositoryAnalysisContext;
import com.gitinbits.dto.response.analysis.FindingType;
import com.gitinbits.dto.response.analysis.HealthFinding;
import com.gitinbits.dto.response.analysis.Severity;
import com.gitinbits.dto.response.repo.BranchDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BranchFindingGenerator implements HealthFindingGenerator {

    @Override
    public List<HealthFinding> generate(RepositoryAnalysisContext context, int score) {
        List<HealthFinding> findings = new ArrayList<>();
        List<BranchDto> branches = context.branches();
        
        if (branches == null || branches.isEmpty()) {
            return findings;
        }

        String defaultBranch = context.repo() != null ? context.repo().defaultBranch() : null;
        boolean defaultIsProtected = false;
        boolean hasReviewRequirement = false;

        for (BranchDto branch : branches) {
            if (branch.name() != null && branch.name().equals(defaultBranch) && branch.isProtected()) {
                defaultIsProtected = true;
            }
            if (Boolean.TRUE.equals(branch.requiredReviews())) {
                hasReviewRequirement = true;
            }
        }

        if (!defaultIsProtected || !hasReviewRequirement) {
            findings.add(new HealthFinding(
                    FindingType.RISK,
                    "Branch Protection Missing",
                    "Important branches do not enforce review requirements or structural protection.",
                    Severity.HIGH,
                    "Enable branch protection and required pull request reviews for the default branch."
            ));
        } else if (score >= 85) {
            findings.add(new HealthFinding(
                    FindingType.STRENGTH,
                    "Secure Branch Policies",
                    "Repository uses strong branch protection rules and safely manages workflows.",
                    Severity.LOW,
                    null
            ));
        }

        return findings;
    }
}
