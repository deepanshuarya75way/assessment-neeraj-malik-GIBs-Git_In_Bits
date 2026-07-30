package com.gitinbits.analysis.repository.finding;

import com.gitinbits.analysis.repository.RepositoryAnalysisContext;
import com.gitinbits.dto.response.analysis.FindingType;
import com.gitinbits.dto.response.analysis.HealthFinding;
import com.gitinbits.dto.response.analysis.Severity;
import com.gitinbits.dto.response.repo.ReviewDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReviewFindingGenerator implements HealthFindingGenerator {

    @Override
    public List<HealthFinding> generate(RepositoryAnalysisContext context, int score) {
        List<HealthFinding> findings = new ArrayList<>();
        List<ReviewDto> reviews = context.reviews();
        
        if (reviews == null || reviews.isEmpty()) {
            return findings;
        }

        if (score >= 80) {
            findings.add(new HealthFinding(
                    FindingType.STRENGTH,
                    "Active Code Review Culture",
                    "Pull requests receive consistent review participation and constructive feedback.",
                    Severity.LOW,
                    null
            ));
        } else if (score < 50) {
            findings.add(new HealthFinding(
                    FindingType.RISK,
                    "Insufficient Review Coverage",
                    "Many changes are merged without meaningful review activity or quality checks.",
                    Severity.HIGH,
                    "Enforce strict review policies and encourage team participation in code review."
            ));
        }

        return findings;
    }
}
