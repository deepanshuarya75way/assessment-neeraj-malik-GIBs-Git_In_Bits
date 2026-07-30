package com.gitinbits.analysis.repository.finding;

import com.gitinbits.analysis.repository.RepositoryAnalysisContext;
import com.gitinbits.dto.response.analysis.FindingType;
import com.gitinbits.dto.response.analysis.HealthFinding;
import com.gitinbits.dto.response.analysis.Severity;
import com.gitinbits.dto.response.repo.CommitDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CommitFindingGenerator implements HealthFindingGenerator {

    @Override
    public List<HealthFinding> generate(RepositoryAnalysisContext context, int score) {
        List<HealthFinding> findings = new ArrayList<>();
        List<CommitDto> commits = context.commits();
        
        if (commits == null || commits.isEmpty()) {
            return findings;
        }

        if (score >= 85) {
            findings.add(new HealthFinding(
                    FindingType.STRENGTH,
                    "Healthy Commit Activity",
                    "Repository shows consistent development activity.",
                    Severity.LOW,
                    null
            ));
        }

        long totalSize = 0;
        int commitsWithSize = 0;

        for (CommitDto commit : commits) {
            int additions = commit.additions() != null ? commit.additions() : 0;
            int deletions = commit.deletions() != null ? commit.deletions() : 0;
            if (additions > 0 || deletions > 0) {
                commitsWithSize++;
            }
            totalSize += (additions + deletions);
        }
        
        double avgSize = commitsWithSize > 0 ? (double) totalSize / commitsWithSize : 0;
        
        if (avgSize > 500) {
            findings.add(new HealthFinding(
                    FindingType.WARNING,
                    "Large Commit Size",
                    "Recent commits contain a high number of file changes and lines of code.",
                    Severity.MEDIUM,
                    "Consider breaking work into smaller, more focused commits."
            ));
        }

        return findings;
    }
}
