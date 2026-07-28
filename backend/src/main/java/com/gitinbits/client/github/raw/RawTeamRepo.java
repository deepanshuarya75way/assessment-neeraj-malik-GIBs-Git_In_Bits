package com.gitinbits.client.github.raw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Raw GitHub API response for a team's repository access entry.
 * Source: GET /orgs/{org}/teams/{team_slug}/repos
 *
 * <p>Internal to the client layer.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RawTeamRepo(
        @JsonProperty("name") String name,
        @JsonProperty("full_name") String fullName,
        @JsonProperty("description") String description,
        @JsonProperty("private") boolean isPrivate,
        @JsonProperty("html_url") String htmlUrl,
        @JsonProperty("permissions") Permissions permissions
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Permissions(
            @JsonProperty("admin") boolean admin,
            @JsonProperty("maintain") boolean maintain,
            @JsonProperty("push") boolean push,
            @JsonProperty("triage") boolean triage,
            @JsonProperty("pull") boolean pull
    ) {}
}
