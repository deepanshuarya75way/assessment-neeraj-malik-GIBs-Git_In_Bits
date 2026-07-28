package com.gitinbits.client.github.raw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Raw GitHub API response for a repository contributor.
 * Source: GET /repos/{owner}/{repo}/contributors
 *
 * <p>NOTE: GitHub returns HTTP 204 with an empty body when a repository
 * has no commits or contributors. The paginator handles this gracefully.
 *
 * <p>Internal to the client layer.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RawContributor(
        @JsonProperty("login") String login,
        @JsonProperty("avatar_url") String avatarUrl,
        @JsonProperty("contributions") Integer contributions,
        @JsonProperty("html_url") String htmlUrl,
        @JsonProperty("type") String type
) {}
