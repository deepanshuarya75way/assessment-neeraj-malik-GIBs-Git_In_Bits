package com.gitinbits.client.github.raw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Raw GitHub API response for a commit.
 * Source: GET /repos/{owner}/{repo}/commits
 *
 * <p>GitHub's commit object has two author layers:
 * <ul>
 *   <li>{@code commit.author} — Git author metadata (name, email, date)</li>
 *   <li>{@code author} — GitHub user who authored the commit (may be null for unlinked emails)</li>
 * </ul>
 * This PoC uses the Git layer ({@code commit.author/committer}) for reliability.
 *
 * <p>Internal to the client layer.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RawCommit(
        @JsonProperty("sha") String sha,
        @JsonProperty("commit") CommitData commit,
        @JsonProperty("html_url") String htmlUrl,
        @JsonProperty("parents") java.util.List<Parent> parents,
        @JsonProperty("stats") Stats stats,
        @JsonProperty("files") java.util.List<File> files,
        @JsonProperty("author") RawUser author
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CommitData(
            @JsonProperty("author") GitPersonData author,
            @JsonProperty("committer") GitPersonData committer,
            @JsonProperty("message") String message,
            @JsonProperty("verification") Verification verification
    ) {}

    /**
     * Git-level identity (name + email + timestamp), not a GitHub user.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GitPersonData(
            @JsonProperty("name") String name,
            @JsonProperty("email") String email,
            @JsonProperty("date") String date
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Verification(
            @JsonProperty("verified") Boolean verified,
            @JsonProperty("reason") String reason
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Parent(
            @JsonProperty("sha") String sha
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Stats(
            @JsonProperty("total") Integer total,
            @JsonProperty("additions") Integer additions,
            @JsonProperty("deletions") Integer deletions
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record File(
            @JsonProperty("filename") String filename
    ) {}
}
