package com.gitinbits.dto.context;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Unified context representing the source of GitHub data requested by the frontend.
 *
 * @param sourceType     The type of data source (e.g., AUTHENTICATED_ORGANIZATION, PUBLIC_ORGANIZATION)
 * @param accountName    The name of the organization or repository owner
 * @param repositoryName The name of the repository (if applicable)
 */
public record GitHubContext(
        @NotNull DataSourceType sourceType,
        @NotBlank String accountName,
        String repositoryName
) {}
