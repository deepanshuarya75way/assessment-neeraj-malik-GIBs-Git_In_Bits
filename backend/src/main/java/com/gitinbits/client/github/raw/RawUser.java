package com.gitinbits.client.github.raw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Raw GitHub API response for the authenticated user.
 * Source: GET /user
 *
 * <p>Internal to the client layer. Services must use
 * {@link com.gitinbits.dto.response.auth.AuthUserDto} instead.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RawUser(
        @JsonProperty("login") String login,
        @JsonProperty("name") String name,
        @JsonProperty("avatar_url") String avatarUrl,
        @JsonProperty("html_url") String htmlUrl,
        @JsonProperty("email") String email
) {}
