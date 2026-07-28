package com.gitinbits.client.github.raw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Raw GitHub API response for a pull request review.
 * Source: GET /repos/{owner}/{repo}/pulls/{pull_number}/reviews
 *
 * <p>Internal to the client layer.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RawReview(
        @JsonProperty("id") Long id,
        @JsonProperty("user") User user,
        @JsonProperty("body") String body,
        @JsonProperty("state") String state,
        @JsonProperty("submitted_at") String submittedAt,
        @JsonProperty("html_url") String htmlUrl,
        @JsonProperty("commit_id") String commitId
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record User(@JsonProperty("login") String login) {}
}
