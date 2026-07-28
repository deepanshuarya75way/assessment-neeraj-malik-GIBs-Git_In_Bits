package com.gitinbits.client.github.raw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Raw GitHub API response for a release.
 * Source: GET /repos/{owner}/{repo}/releases
 *
 * <p>Internal to the client layer.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RawRelease(
        @JsonProperty("id") Long id,
        @JsonProperty("tag_name") String tagName,
        @JsonProperty("name") String name,
        @JsonProperty("body") String body,
        @JsonProperty("draft") boolean draft,
        @JsonProperty("prerelease") boolean prerelease,
        @JsonProperty("published_at") String publishedAt,
        @JsonProperty("created_at") String createdAt,
        @JsonProperty("html_url") String htmlUrl,
        @JsonProperty("author") Author author
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Author(@JsonProperty("login") String login) {}
}
