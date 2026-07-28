package com.gitinbits.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

/**
 * HTTP client configuration.
 *
 * <p>Configures a {@link RestClient} bean pre-set with GitHub API default headers.
 * OAuth2 token injection is handled per-request in {@link com.gitinbits.client.github.GitHubClientImpl}.
 */
@Configuration
public class RestClientConfig {

    private final GitHubProperties gitHubProperties;

    public RestClientConfig(GitHubProperties gitHubProperties) {
        this.gitHubProperties = gitHubProperties;
    }

    @Bean
    public RestClient gitHubRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl(gitHubProperties.getBaseUrl())
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .defaultHeader("X-GitHub-Api-Version", gitHubProperties.getApiVersion())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
