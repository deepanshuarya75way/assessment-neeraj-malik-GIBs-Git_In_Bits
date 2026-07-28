package com.gitinbits.client.github.raw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Raw GitHub API response for an issue.
 * Source: GET /repos/{owner}/{repo}/issues?state=all
 *
 * <p>IMPORTANT: GitHub's issues endpoint returns both issues AND pull requests.
 * The {@code pullRequest} field is non-null for PRs and must be used to filter
 * them out when the caller only wants pure issues.
 *
 * <p>Internal to the client layer.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RawIssue(
        @JsonProperty("number") Integer number,
        @JsonProperty("title") String title,
        @JsonProperty("body") String body,
        @JsonProperty("labels") List<Label> labels,
        @JsonProperty("assignees") List<Assignee> assignees,
        @JsonProperty("state") String state,
        @JsonProperty("locked") Boolean locked,
        @JsonProperty("comments") Integer comments,
        @JsonProperty("milestone") Milestone milestone,
        @JsonProperty("created_at") String createdAt,
        @JsonProperty("updated_at") String updatedAt,
        @JsonProperty("closed_at") String closedAt,
        @JsonProperty("pull_request") Object pullRequest
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Milestone(@JsonProperty("title") String title) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Label(@JsonProperty("name") String name) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Assignee(@JsonProperty("login") String login) {}

    /**
     * Returns true if this item is a pull request masquerading as an issue.
     * GitHub's issues endpoint includes PRs; filter these out to return only real issues.
     */
    public boolean isPullRequest() {
        return pullRequest != null;
    }
}
