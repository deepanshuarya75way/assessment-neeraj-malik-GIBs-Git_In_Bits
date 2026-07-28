package com.gitinbits.client.github.raw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Raw GitHub API response for a pull request review comment (inline code comment).
 * Source: GET /repos/{owner}/{repo}/pulls/{pull_number}/comments
 *
 * <p>Internal to the client layer.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RawReviewComment(
        @JsonProperty("id") Long id,
        @JsonProperty("user") User user,
        @JsonProperty("body") String body,
        @JsonProperty("path") String path,
        @JsonProperty("line") Integer line,
        @JsonProperty("original_line") Integer originalLine,
        @JsonProperty("diff_hunk") String diffHunk,
        @JsonProperty("created_at") String createdAt,
        @JsonProperty("updated_at") String updatedAt,
        @JsonProperty("html_url") String htmlUrl
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record User(@JsonProperty("login") String login) {}
}
