package com.gitinbits.analysis.repository.finding;

import com.gitinbits.analysis.repository.RepositoryAnalysisContext;
import com.gitinbits.dto.response.analysis.FindingType;
import com.gitinbits.dto.response.analysis.HealthFinding;
import com.gitinbits.dto.response.analysis.Severity;
import com.gitinbits.dto.response.repo.PullRequestDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PullRequestFindingGenerator implements HealthFindingGenerator {

    @Override
    public List<HealthFinding> generate(RepositoryAnalysisContext context, int score) {
        List<HealthFinding> findings = new ArrayList<>();
        List<PullRequestDto> prs = context.pullRequests();
        
        if (prs == null || prs.isEmpty()) {
            return findings;
        }

        if (score >= 80) {
            findings.add(new HealthFinding(
                    FindingType.STRENGTH,
                    "Strong Pull Request Workflow",
                    "Most pull requests receive adequate reviews before merging.",
                    Severity.LOW,
                    null
            ));
        } else if (score < 60) {
            findings.add(new HealthFinding(
                    FindingType.WARNING,
                    "Low Review Participation",
                    "Many pull requests are merged with limited or no review activity.",
                    Severity.MEDIUM,
                    "Ensure team members actively review pull requests before they are merged."
            ));
        }

        return findings;
    }
}
