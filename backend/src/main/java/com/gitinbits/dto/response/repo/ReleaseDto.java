package com.gitinbits.dto.response.repo;

/**
 * Release metadata.
 * Returned as items in GET /api/repos/{repo}/releases.
 *
 * @param version      Semantic version tag (e.g., "v2.1.0")
 * @param tag          Git tag name
 * @param name         Release display name
 * @param releaseNotes Markdown-formatted release notes (body)
 * @param draft        Whether this is an unpublished draft release
 * @param prerelease   Whether this is marked as a pre-release
 * @param publishedAt  ISO-8601 publication timestamp
 */
public record ReleaseDto(
        String version,
        String tag,
        String name,
        String releaseNotes,
        boolean draft,
        boolean prerelease,
        String publishedAt,
        String author
) {}
