package com.gitinbits.dto.response.repo;

import java.util.List;

/**
 * Repository metadata.
 * Returned as items in GET /api/repos and as single response in GET /api/repos/{repo}.
 *
 * @param name            Repository name
 * @param fullName        Owner/repo (e.g., "octocat/Hello-World")
 * @param description     Repository description
 * @param visibility      "public", "private", or "internal"
 * @param defaultBranch   Default branch name (e.g., "main")
 * @param language        Primary programming language
 * @param htmlUrl         GitHub URL for the repository
 * @param cloneUrl        HTTPS clone URL
 * @param sshUrl          SSH clone URL
 * @param fork            Whether this is a fork
 * @param archived        Whether the repository is archived
 * @param topics          GitHub topics/tags applied to the repository
 * @param openIssuesCount Number of open issues
 * @param forksCount      Number of forks
 * @param stargazersCount Number of stargazers
 * @param watchersCount   Number of watchers
 * @param sizeKb          Repository size in kilobytes
 * @param createdAt       ISO-8601 creation timestamp
 * @param updatedAt       ISO-8601 last-updated timestamp
 * @param pushedAt        ISO-8601 timestamp of last push
 */
public record RepoDto(
        String name,
        String fullName,
        String description,
        String visibility,
        String defaultBranch,
        String language,
        String htmlUrl,
        String cloneUrl,
        String sshUrl,
        boolean fork,
        boolean archived,
        List<String> topics,
        Integer openIssuesCount,
        Integer forksCount,
        Integer stargazersCount,
        Integer watchersCount,
        Integer sizeKb,
        String createdAt,
        String updatedAt,
        String pushedAt,
        String homepage,
        Integer networkCount,
        Integer subscribersCount,
        String licenseName
) {}
