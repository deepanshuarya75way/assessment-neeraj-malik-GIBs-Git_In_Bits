package com.gitinbits.dto.response.team;

import java.util.List;

/**
 * Team metadata including members and repository access.
 * Returned as items in GET /api/teams.
 *
 * <p>Note: Fetching team members and team repos requires two additional API calls
 * per team (GitHub does not return them inline). This is by design for this PoC.
 *
 * @param name        Team display name
 * @param slug        URL-safe team identifier (used internally for API calls)
 * @param description Team description
 * @param privacy     "closed" or "secret"
 * @param members     GitHub usernames of team members
 * @param repos       Repository names the team has access to
 */
public record TeamDto(
        String name,
        String slug,
        String description,
        String privacy,
        String parentTeamName,
        List<String> members,
        List<TeamRepoAccess> repos
) {
    public record TeamRepoAccess(
            String repoName,
            boolean admin,
            boolean maintain,
            boolean push,
            boolean triage,
            boolean pull
    ) {}
}
