package com.gitinbits.dto.response.repo;

/**
 * Inline pull request review comment (code-level comment on a specific line/file).
 * Returned as items in GET /api/repos/{repo}/pulls/{number}/comments.
 *
 * @param id           Unique comment ID
 * @param reviewer     GitHub login of the commenter
 * @param body         Comment text
 * @param path         Relative file path the comment is anchored to
 * @param line         Line number in the file (current version)
 * @param originalLine Line number in the original diff
 * @param createdAt    ISO-8601 creation timestamp
 */
public record ReviewCommentDto(
        Long id,
        String reviewer,
        String body,
        String path,
        Integer line,
        Integer originalLine,
        String createdAt,
        String updatedAt
) {}
