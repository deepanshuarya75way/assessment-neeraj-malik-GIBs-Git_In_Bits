package com.gitinbits.dto.response.repo;

/**
 * Branch metadata.
 * Returned as items in GET /api/repos/{repo}/branches.
 *
 * @param name            Branch name (e.g., "main", "develop")
 * @param isProtected     Whether branch protection rules are active
 * @param latestCommitSha SHA of the most recent commit on this branch
 */
public record BranchDto(
        String name,
        boolean isProtected,
        String latestCommitSha,
        String latestCommitTimestamp,
        String latestCommitAuthor,
        Boolean requiredStatusChecks,
        Boolean requiredReviews,
        Boolean forcePushAllowed,
        Boolean deletionAllowed
) {}
