package com.gitinbits.analysis.repository.finding;

import com.gitinbits.analysis.repository.RepositoryAnalysisContext;
import com.gitinbits.dto.response.analysis.FindingType;
import com.gitinbits.dto.response.analysis.HealthFinding;
import com.gitinbits.dto.response.analysis.Severity;
import com.gitinbits.dto.response.repo.ContributorDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ContributorFindingGenerator implements HealthFindingGenerator {

    @Override
    public List<HealthFinding> generate(RepositoryAnalysisContext context, int score) {
        List<HealthFinding> findings = new ArrayList<>();
        List<ContributorDto> contributors = context.contributors();
        
        if (contributors == null || contributors.isEmpty()) {
            return findings;
        }

        if (contributors.size() == 1) {
             findings.add(new HealthFinding(
                    FindingType.WARNING,
                    "Single Contributor Dependency",
                    "Repository activity is entirely dependent on a single contributor.",
                    Severity.MEDIUM,
                    "Encourage broader contributor participation to reduce the bus factor."
            ));
             return findings;
        }

        long maxContributions = 0;
        long totalContributions = 0;

        for (ContributorDto contributor : contributors) {
            long count = contributor.contributions() != null ? contributor.contributions() : 0;
            totalContributions += count;
            if (count > maxContributions) {
                maxContributions = count;
            }
        }

        if (totalContributions > 0) {
            double ratio = (double) maxContributions / totalContributions;
            if (ratio > 0.80) {
                findings.add(new HealthFinding(
                        FindingType.WARNING,
                        "High Contributor Dependency",
                        "Repository activity depends heavily on a small number of contributors.",
                        Severity.MEDIUM,
                        "Encourage broader contributor participation."
                ));
            }
        }

        if (score >= 85) {
            findings.add(new HealthFinding(
                    FindingType.STRENGTH,
                    "Healthy Community Ecosystem",
                    "Repository benefits from a diverse and active pool of contributors.",
                    Severity.LOW,
                    null
            ));
        }

        return findings;
    }
}
