package com.gitinbits.client.github.raw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RawCollaborator(
        @JsonProperty("login") String login,
        @JsonProperty("avatar_url") String avatarUrl,
        @JsonProperty("html_url") String htmlUrl,
        @JsonProperty("role_name") String roleName,
        @JsonProperty("permissions") Permissions permissions
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Permissions(
            @JsonProperty("admin") boolean admin,
            @JsonProperty("maintain") boolean maintain,
            @JsonProperty("push") boolean push,
            @JsonProperty("triage") boolean triage,
            @JsonProperty("pull") boolean pull
    ) {}
}
