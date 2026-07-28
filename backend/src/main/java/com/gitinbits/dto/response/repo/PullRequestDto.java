package com.gitinbits.dto.response.repo;

/**
 * Pull request metadata.
 * Returned as items in GET /api/repos/{repo}/pulls.
 *
 * @param number      PR number within the repository
 * @param title       PR title
 * @param description PR body/description
 * @param author      GitHub login of the PR author
 * @param state       "open" or "closed"
 * @param createdAt   ISO-8601 creation timestamp
 * @param closedAt    ISO-8601 close timestamp (null if still open)
 * @param mergedAt    ISO-8601 merge timestamp (null if not merged)
 * @param baseBranch  Target branch (e.g., "main")
 * @param headBranch  Source branch (e.g., "feature/new-login")
 */
public record PullRequestDto(
        Integer number,
        String title,
        String description,
        String author,
        String state,
        Boolean locked,
        Boolean draft,
        String createdAt,
        String updatedAt,
        String closedAt,
        String mergedAt,
        String mergeCommitSha,
        String mergeableState,
        java.util.List<String> assignees,
        java.util.List<String> requestedReviewers,
        java.util.List<String> labels,
        String milestone,
        Integer commitsCount,
        Integer changedFilesCount,
        Integer additions,
        Integer deletions,
        Integer reviewCount,
        Integer commentCount,
        String baseBranch,
        String headBranch
) {}
