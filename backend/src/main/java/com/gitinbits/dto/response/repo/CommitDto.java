package com.gitinbits.dto.response.repo;

/**
 * Commit metadata.
 * Returned as items in GET /api/repos/{repo}/commits.
 *
 * <p>Uses Git-level author and committer data (from the {@code commit.author}
 * and {@code commit.committer} fields) rather than the GitHub user objects,
 * which may be null for commits with unlinked email addresses.
 *
 * @param sha             Full commit SHA
 * @param authorName      Git author's display name
 * @param authorEmail     Git author's email address
 * @param committerName   Git committer's display name
 * @param committerEmail  Git committer's email address
 * @param message         Commit message (may be multi-line)
 * @param timestamp       ISO-8601 author date
 * @param url             GitHub URL to view this commit
 */
public record CommitDto(
        String sha,
        String authorName,
        String authorEmail,
        String committerName,
        String committerEmail,
        String message,
        String timestamp,
        String url,
        java.util.List<String> parentShas,
        Boolean verified,
        String verificationReason,
        Integer filesChangedCount,
        Integer additions,
        Integer deletions,
        Integer totalChanges,
        java.util.List<String> changedFileNames
) {}
