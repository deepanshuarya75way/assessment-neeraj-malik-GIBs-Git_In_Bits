package com.gitinbits.client.github.raw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RawWorkflowRun(
        @JsonProperty("name") String name,
        @JsonProperty("head_branch") String headBranch,
        @JsonProperty("status") String status,
        @JsonProperty("conclusion") String conclusion,
        @JsonProperty("event") String event,
        @JsonProperty("created_at") String createdAt,
        @JsonProperty("updated_at") String updatedAt
) {}
