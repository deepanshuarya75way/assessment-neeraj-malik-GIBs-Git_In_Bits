package com.gitinbits.client.github.raw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Raw GitHub API response for an organization.
 * Sources:
 *   GET /user/orgs          (list — uses abbreviated fields)
 *   GET /orgs/{org}         (full detail)
 *
 * <p>Internal to the client layer.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RawOrg(
        @JsonProperty("login") String login,
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("avatar_url") String avatarUrl,
        @JsonProperty("html_url") String htmlUrl,
        @JsonProperty("url") String url,
        @JsonProperty("public_repos") Integer publicRepos
) {}
