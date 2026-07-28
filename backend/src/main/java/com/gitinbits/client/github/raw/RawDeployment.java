package com.gitinbits.client.github.raw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RawDeployment(
        @JsonProperty("id") Long id,
        @JsonProperty("environment") String environment,
        @JsonProperty("creator") Creator creator,
        @JsonProperty("created_at") String createdAt,
        @JsonProperty("updated_at") String updatedAt,
        @JsonProperty("repository_url") String repositoryUrl
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Creator(@JsonProperty("login") String login) {}
}
