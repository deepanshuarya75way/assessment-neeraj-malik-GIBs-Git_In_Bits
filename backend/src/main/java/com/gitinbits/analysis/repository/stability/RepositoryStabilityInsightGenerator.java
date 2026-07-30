package com.gitinbits.analysis.repository.stability;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class RepositoryStabilityInsightGenerator {

    public List<RepositoryStabilityInsight> generateStrengths(RepositoryStabilityBreakdown breakdown) {
        List<RepositoryStabilityInsight> strengths = new ArrayList<>();
        
        if (breakdown.branchStabilityScore() >= 90) {
            strengths.add(new RepositoryStabilityInsight(
                    "Protected Branches",
                    "Branch protection rules are consistently enforced across the repository.",
                    InsightSeverity.INFO
            ));
        }
        
        if (breakdown.workflowStabilityScore() >= 90) {
            strengths.add(new RepositoryStabilityInsight(
                    "Reliable CI Pipeline",
                    "Workflow executions are consistently successful.",
                    InsightSeverity.INFO
            ));
        }
        
        if (breakdown.releaseStabilityScore() >= 90) {
            strengths.add(new RepositoryStabilityInsight(
                    "Consistent Release Cadence",
                    "Releases occur regularly with healthy deployment frequency.",
                    InsightSeverity.INFO
            ));
        }
        
        if (breakdown.commitStabilityScore() >= 85) {
            strengths.add(new RepositoryStabilityInsight(
                    "Stable Development Activity",
                    "Commit activity is regular without excessive volatility.",
                    InsightSeverity.INFO
            ));
        }
        
        if (breakdown.deploymentStabilityScore() >= 85) {
            strengths.add(new RepositoryStabilityInsight(
                    "Predictable Deployments",
                    "Deployments are rolled out in a regular, mature cadence.",
                    InsightSeverity.INFO
            ));
        }
        
        if (breakdown.contributorStabilityScore() >= 85) {
            strengths.add(new RepositoryStabilityInsight(
                    "Distributed Contributor Dependency",
                    "Repository development is distributed across multiple contributors.",
                    InsightSeverity.INFO
            ));
        }

        return strengths;
    }

    public List<RepositoryStabilityInsight> generateConcerns(RepositoryStabilityBreakdown breakdown) {
        List<RepositoryStabilityInsight> concerns = new ArrayList<>();

        if (breakdown.branchStabilityScore() < 60) {
            concerns.add(new RepositoryStabilityInsight(
                    "Branch Protection Weak",
                    "Some important branches lack sufficient protection.",
                    InsightSeverity.WARNING
            ));
        }

        if (breakdown.workflowStabilityScore() < 60) {
            concerns.add(new RepositoryStabilityInsight(
                    "CI Reliability Issues",
                    "Multiple workflow failures reduce repository stability.",
                    InsightSeverity.WARNING
            ));
        }

        if (breakdown.releaseStabilityScore() < 60) {
            concerns.add(new RepositoryStabilityInsight(
                    "Irregular Releases",
                    "Release cadence appears inconsistent.",
                    InsightSeverity.WARNING
            ));
        }

        if (breakdown.commitStabilityScore() < 50) {
            concerns.add(new RepositoryStabilityInsight(
                    "Unstable Development Pattern",
                    "Development activity is highly volatile.",
                    InsightSeverity.CRITICAL
            ));
        }
        
        if (breakdown.deploymentStabilityScore() < 50) {
            concerns.add(new RepositoryStabilityInsight(
                    "Stagnant Deployments",
                    "Deployments are infrequent or very old.",
                    InsightSeverity.WARNING
            ));
        }
        
        if (breakdown.contributorStabilityScore() < 50) {
            concerns.add(new RepositoryStabilityInsight(
                    "High Contributor Risk",
                    "Development is heavily dependent on a single contributor.",
                    InsightSeverity.CRITICAL
            ));
        }

        return concerns;
    }

    public List<RepositoryStabilityInsight> generateRecommendations(RepositoryStabilityBreakdown breakdown) {
        List<RepositoryStabilityInsight> recommendations = new ArrayList<>();

        if (breakdown.branchStabilityScore() < 60) {
            recommendations.add(new RepositoryStabilityInsight(
                    "Enable branch protection",
                    "Enable branch protection on critical branches.",
                    InsightSeverity.WARNING
            ));
            recommendations.add(new RepositoryStabilityInsight(
                    "Clean inactive branches",
                    "Delete inactive branches after merge.",
                    InsightSeverity.INFO
            ));
        }

        if (breakdown.workflowStabilityScore() < 60) {
            recommendations.add(new RepositoryStabilityInsight(
                    "Investigate CI failures",
                    "Investigate failing CI pipelines.",
                    InsightSeverity.WARNING
            ));
        }

        if (breakdown.releaseStabilityScore() < 60) {
            recommendations.add(new RepositoryStabilityInsight(
                    "Stabilize release cadence",
                    "Adopt a more predictable release schedule.",
                    InsightSeverity.WARNING
            ));
        }

        if (breakdown.commitStabilityScore() < 50) {
            recommendations.add(new RepositoryStabilityInsight(
                    "Improve commit practices",
                    "Encourage smaller, more frequent commits.",
                    InsightSeverity.CRITICAL
            ));
        }

        if (breakdown.contributorStabilityScore() < 50) {
            recommendations.add(new RepositoryStabilityInsight(
                    "Distribute knowledge",
                    "Encourage more team members to contribute to reduce bus factor.",
                    InsightSeverity.CRITICAL
            ));
        }

        // Sort by severity (CRITICAL -> WARNING -> INFO)
        // Since enum order is INFO (0), WARNING (1), CRITICAL (2), reversed() sorts descending
        recommendations.sort(Comparator.comparing(RepositoryStabilityInsight::severity).reversed());

        return recommendations;
    }
}
