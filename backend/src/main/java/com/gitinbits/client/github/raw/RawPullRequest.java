package com.gitinbits.client.github.raw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Raw GitHub API response for a pull request.
 * Source: GET /repos/{owner}/{repo}/pulls?state=all
 *
 * <p>Internal to the client layer.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RawPullRequest(
        @JsonProperty("number") Integer number,
        @JsonProperty("title") String title,
        @JsonProperty("body") String body,
        @JsonProperty("user") User user,
        @JsonProperty("state") String state,
        @JsonProperty("locked") Boolean locked,
        @JsonProperty("draft") Boolean draft,
        @JsonProperty("created_at") String createdAt,
        @JsonProperty("updated_at") String updatedAt,
        @JsonProperty("closed_at") String closedAt,
        @JsonProperty("merged_at") String mergedAt,
        @JsonProperty("merge_commit_sha") String mergeCommitSha,
        @JsonProperty("mergeable_state") String mergeableState,
        @JsonProperty("assignees") java.util.List<User> assignees,
        @JsonProperty("requested_reviewers") java.util.List<User> requestedReviewers,
        @JsonProperty("labels") java.util.List<Label> labels,
        @JsonProperty("milestone") Milestone milestone,
        @JsonProperty("commits") Integer commits,
        @JsonProperty("additions") Integer additions,
        @JsonProperty("deletions") Integer deletions,
        @JsonProperty("changed_files") Integer changedFiles,
        @JsonProperty("comments") Integer comments,
        @JsonProperty("review_comments") Integer reviewComments,
        @JsonProperty("base") BranchRef base,
        @JsonProperty("head") BranchRef head
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Label(
            @JsonProperty("name") String name
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Milestone(
            @JsonProperty("title") String title
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record User(@JsonProperty("login") String login) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record BranchRef(@JsonProperty("ref") String ref) {}
}
