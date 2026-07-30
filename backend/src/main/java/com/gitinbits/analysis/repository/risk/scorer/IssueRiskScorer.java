package com.gitinbits.analysis.repository.risk.scorer;

import com.gitinbits.dto.response.repo.IssueDto;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Evaluates issue management risk.
 * Higher score = Higher risk.
 */
@Component
public class IssueRiskScorer {
    public int score(List<IssueDto> issues) {
        if (issues == null || issues.isEmpty()) {
            return 50; // Neutral risk if issues aren't used
        }

        double riskScore = 0.0;
        int openIssues = 0;
        int oldOpenIssues = 0;
        int unassignedIssues = 0;
        
        Instant now = Instant.now();

        for (IssueDto issue : issues) {
            if ("open".equalsIgnoreCase(issue.state())) {
                openIssues++;
                
                if (issue.assignees() == null || issue.assignees().isEmpty()) {
                    unassignedIssues++;
                }

                try {
                    Instant createdAt = Instant.parse(issue.createdAt());
                    if (ChronoUnit.DAYS.between(createdAt, now) > 90) {
                        oldOpenIssues++;
                    }
                } catch (Exception ignored) {}
            }
        }

        double openRatio = (double) openIssues / issues.size();
        
        // 1. Open Issue Volume Risk (Max 30)
        riskScore += Math.min(30, openRatio * 40);

        if (openIssues > 0) {
            double oldRatio = (double) oldOpenIssues / openIssues;
            double unassignedRatio = (double) unassignedIssues / openIssues;
            
            // 2. Old Unresolved Issues Risk (Max 40)
            riskScore += Math.min(40, oldRatio * 50);
            
            // 3. Unassigned Issues Risk (Max 30)
            riskScore += Math.min(30, unassignedRatio * 40);
        }

        return (int) Math.max(0, Math.min(100, Math.round(riskScore)));
    }
}
