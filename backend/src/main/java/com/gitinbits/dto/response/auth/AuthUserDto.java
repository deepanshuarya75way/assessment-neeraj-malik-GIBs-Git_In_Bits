package com.gitinbits.dto.response.auth;

/**
 * Public DTO representing the currently authenticated GitHub user.
 * Returned by GET /api/auth/me.
 *
 * @param login     GitHub username (e.g., "octocat")
 * @param name      Display name (may be null if not set)
 * @param avatarUrl URL to the user's avatar image
 * @param htmlUrl   GitHub profile URL
 * @param email     Public email address (may be null)
 */
public record AuthUserDto(
        String login,
        String name,
        String avatarUrl,
        String htmlUrl,
        String email
) {}
