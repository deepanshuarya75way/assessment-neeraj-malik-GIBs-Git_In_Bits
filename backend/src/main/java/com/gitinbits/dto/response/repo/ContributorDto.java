package com.gitinbits.dto.response.repo;

/**
 * Contributor metadata.
 * Returned as items in GET /api/repos/{repo}/contributors.
 *
 * @param username      GitHub login of the contributor
 * @param avatarUrl     URL to the contributor's avatar
 * @param contributions Total number of commits attributed to this contributor
 */
public record ContributorDto(
        String username,
        String avatarUrl,
        String profileUrl,
        Integer contributions
) {}
