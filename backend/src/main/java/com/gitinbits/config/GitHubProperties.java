package com.gitinbits.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Strongly-typed configuration properties for GitHub API access.
 *
 * <p>Bound from the {@code github.*} namespace in {@code application.yml}.
 * Validated at startup so misconfiguration fails fast.
 */
@Validated
@ConfigurationProperties(prefix = "github")
public class GitHubProperties {

    /**
     * GitHub REST API base URL.
     * Defaults to {@code https://api.github.com}.
     */
    @NotBlank
    private String baseUrl;

    /**
     * GitHub API version header value sent on every request.
     * See: https://docs.github.com/en/rest/overview/api-versions
     */
    @NotBlank
    private String apiVersion;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }
}
