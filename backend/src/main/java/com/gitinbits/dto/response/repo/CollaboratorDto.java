package com.gitinbits.dto.response.repo;

public record CollaboratorDto(
        String username,
        String avatarUrl,
        String profileUrl,
        String roleName,
        Permissions permissions
) {
    public record Permissions(
            boolean admin,
            boolean maintain,
            boolean push,
            boolean triage,
            boolean pull
    ) {}
}
