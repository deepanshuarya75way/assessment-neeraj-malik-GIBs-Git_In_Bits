package com.gitinbits.dto.response.repo;

import java.util.List;

/**
 * Issue metadata.
 * Returned as items in GET /api/repos/{repo}/issues.
 *
 * <p>Only genuine issues are included. GitHub's API returns pull requests
 * on the issues endpoint as well; those are filtered out server-side.
 *
 * @param number     Issue number within the repository
 * @param title      Issue title
 * @param description Issue body/description
 * @param labels     List of label names applied to the issue
 * @param assignees  List of GitHub usernames assigned to the issue
 * @param state      "open" or "closed"
 * @param createdAt  ISO-8601 creation timestamp
 */
public record IssueDto(
        Integer number,
        String title,
        String description,
        List<String> labels,
        List<String> assignees,
        String milestone,
        String state,
        Boolean locked,
        Integer commentCount,
        String createdAt,
        String updatedAt,
        String closedAt
) {}
