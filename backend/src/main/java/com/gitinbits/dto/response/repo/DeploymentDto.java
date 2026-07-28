package com.gitinbits.dto.response.repo;

public record DeploymentDto(
        Long id,
        String environment,
        String state,
        String creator,
        String createdAt,
        String updatedAt,
        String repositoryUrl
) {}
