package com.gitinbits.client.github.raw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Raw GitHub API response for a repository.
 * Sources:
 *   GET /orgs/{org}/repos          (list)
 *   GET /repos/{owner}/{repo}      (single)
 *
 * <p>Internal to the client layer.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RawRepo(
        @JsonProperty("name") String name,
        @JsonProperty("full_name") String fullName,
        @JsonProperty("description") String description,
        @JsonProperty("private") boolean isPrivate,
        @JsonProperty("visibility") String visibility,
        @JsonProperty("default_branch") String defaultBranch,
        @JsonProperty("language") String language,
        @JsonProperty("html_url") String htmlUrl,
        @JsonProperty("clone_url") String cloneUrl,
        @JsonProperty("ssh_url") String sshUrl,
        @JsonProperty("fork") boolean fork,
        @JsonProperty("archived") boolean archived,
        @JsonProperty("disabled") boolean disabled,
        @JsonProperty("open_issues_count") Integer openIssuesCount,
        @JsonProperty("forks_count") Integer forksCount,
        @JsonProperty("stargazers_count") Integer stargazersCount,
        @JsonProperty("watchers_count") Integer watchersCount,
        @JsonProperty("size") Integer size,
        @JsonProperty("topics") java.util.List<String> topics,
        @JsonProperty("created_at") String createdAt,
        @JsonProperty("updated_at") String updatedAt,
        @JsonProperty("pushed_at") String pushedAt,
        @JsonProperty("homepage") String homepage,
        @JsonProperty("network_count") Integer networkCount,
        @JsonProperty("subscribers_count") Integer subscribersCount,
        @JsonProperty("license") License license
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record License(
            @JsonProperty("key") String key,
            @JsonProperty("name") String name,
            @JsonProperty("spdx_id") String spdxId,
            @JsonProperty("url") String url,
            @JsonProperty("node_id") String nodeId
    ) {}
}
