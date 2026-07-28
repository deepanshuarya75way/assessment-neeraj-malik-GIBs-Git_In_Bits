package com.gitinbits.dto.response.repo;

/**
 * Pull request review metadata.
 * Returned as items in GET /api/repos/{repo}/pulls/{number}/reviews.
 *
 * @param reviewer    GitHub login of the reviewer
 * @param state       Review state: APPROVED, CHANGES_REQUESTED, COMMENTED, DISMISSED, PENDING
 * @param submittedAt ISO-8601 timestamp when the review was submitted
 * @param body        Review summary comment body (may be empty)
 */
public record ReviewDto(
        String reviewer,
        String state,
        String submittedAt,
        String body,
        String commitSha
) {}
