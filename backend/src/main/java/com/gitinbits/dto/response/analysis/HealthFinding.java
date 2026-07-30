package com.gitinbits.dto.response.analysis;

/**
 * Represents a human-readable explanation or recommendation based on numerical health scores.
 *
 * @param type           The category of finding (e.g., STRENGTH, WARNING)
 * @param title          Short descriptive title for the finding
 * @param description    Detailed explanation of the finding
 * @param severity       Impact or urgency level
 * @param recommendation Optional actionable recommendation
 */
public record HealthFinding(
        FindingType type,
        String title,
        String description,
        Severity severity,
        String recommendation
) {}
