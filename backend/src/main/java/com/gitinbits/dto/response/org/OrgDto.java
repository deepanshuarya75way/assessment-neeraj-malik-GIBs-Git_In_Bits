package com.gitinbits.dto.response.org;

/**
 * Full organization details.
 * Returned by GET /api/org.
 *
 * <p>Note: GitHub's /orgs/{org} endpoint does not expose member count directly.
 * Member count is therefore not included to avoid any calculation (listing + counting)
 * which is outside the PoC scope.
 *
 * @param name          Organization display name
 * @param login         GitHub organization login (unique identifier)
 * @param description   Organization description
 * @param avatarUrl     URL to the organization's avatar
 * @param htmlUrl       GitHub organization profile URL
 * @param publicRepoCount Number of public repositories
 */
public record OrgDto(
        String name,
        String login,
        String description,
        String avatarUrl,
        String htmlUrl,
        Integer publicRepoCount
) {}
