package com.gitinbits.client.github.raw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Raw GitHub API response for a branch.
 * Source: GET /repos/{owner}/{repo}/branches
 *
 * <p>Internal to the client layer.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RawBranch(
        @JsonProperty("name") String name,
        @JsonProperty("protected") boolean isProtected,
        @JsonProperty("commit") CommitRef commit,
        @JsonProperty("protection") Protection protection
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Protection(
            @JsonProperty("required_status_checks") RequiredStatusChecks requiredStatusChecks,
            @JsonProperty("required_pull_request_reviews") RequiredReviews requiredPullRequestReviews,
            @JsonProperty("allow_force_pushes") AllowConfig allowForcePushes,
            @JsonProperty("allow_deletions") AllowConfig allowDeletions
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RequiredStatusChecks(
            @JsonProperty("strict") Boolean strict
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RequiredReviews(
            @JsonProperty("dismiss_stale_reviews") Boolean dismissStaleReviews
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AllowConfig(
            @JsonProperty("enabled") Boolean enabled
    ) {}
    /**
     * The abbreviated commit reference on the branch tip.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CommitRef(
            @JsonProperty("sha") String sha,
            @JsonProperty("url") String url
    ) {}
}
