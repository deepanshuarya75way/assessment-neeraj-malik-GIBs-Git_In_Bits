package com.gitinbits.dto.response.org;

/**
 * Abbreviated organization summary used in the organization picker list.
 * Returned as items in GET /api/auth/orgs.
 *
 * @param login       GitHub organization login (e.g., "google")
 * @param description Organization description
 * @param avatarUrl   URL to the organization's avatar image
 * @param htmlUrl     GitHub organization profile URL
 */
public record OrgSummaryDto(
        String login,
        String description,
        String avatarUrl,
        String htmlUrl
) {}
