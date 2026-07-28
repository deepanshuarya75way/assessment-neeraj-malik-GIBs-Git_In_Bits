package com.gitinbits.client.github.raw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Raw GitHub API response for an organization team.
 * Source: GET /orgs/{org}/teams
 *
 * <p>The {@code slug} field is required to make secondary API calls
 * for team members ({@code /orgs/{org}/teams/{slug}/members})
 * and team repositories ({@code /orgs/{org}/teams/{slug}/repos}).
 *
 * <p>Internal to the client layer.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RawTeam(
        @JsonProperty("id") Long id,
        @JsonProperty("name") String name,
        @JsonProperty("slug") String slug,
        @JsonProperty("description") String description,
        @JsonProperty("privacy") String privacy,
        @JsonProperty("members_count") Integer membersCount,
        @JsonProperty("repos_count") Integer reposCount,
        @JsonProperty("parent") ParentTeam parent
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ParentTeam(
            @JsonProperty("name") String name,
            @JsonProperty("slug") String slug
    ) {}
}
