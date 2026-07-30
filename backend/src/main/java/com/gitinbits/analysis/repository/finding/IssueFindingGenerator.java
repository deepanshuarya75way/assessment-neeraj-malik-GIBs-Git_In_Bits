package com.gitinbits.analysis.repository.finding;

import com.gitinbits.analysis.repository.RepositoryAnalysisContext;
import com.gitinbits.dto.response.analysis.FindingType;
import com.gitinbits.dto.response.analysis.HealthFinding;
import com.gitinbits.dto.response.analysis.Severity;
import com.gitinbits.dto.response.repo.IssueDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class IssueFindingGenerator implements HealthFindingGenerator {

    @Override
    public List<HealthFinding> generate(RepositoryAnalysisContext context, int score) {
        List<HealthFinding> findings = new ArrayList<>();
        List<IssueDto> issues = context.issues();
        
        if (issues == null || issues.isEmpty()) {
            return findings;
        }

        long openCount = issues.stream().filter(i -> "open".equalsIgnoreCase(i.state())).count();
        double openRatio = (double) openCount / issues.size();

        if (openRatio > 0.50 && openCount > 5) {
            findings.add(new HealthFinding(
                    FindingType.WARNING,
                    "Growing Issue Backlog",
                    "Repository has a high number of unresolved issues compared to closed issues.",
                    Severity.MEDIUM,
                    "Review and prioritize stale issues for closure or assignment."
            ));
        }

        if (score >= 85) {
            findings.add(new HealthFinding(
                    FindingType.STRENGTH,
                    "Effective Issue Resolution",
                    "The community consistently resolves and organizes issues.",
                    Severity.LOW,
                    null
            ));
        }

        return findings;
    }
}
