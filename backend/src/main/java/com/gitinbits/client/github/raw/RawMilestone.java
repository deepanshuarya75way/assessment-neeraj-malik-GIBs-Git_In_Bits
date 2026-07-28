package com.gitinbits.client.github.raw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RawMilestone(
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("due_on") String dueOn,
        @JsonProperty("state") String state,
        @JsonProperty("open_issues") Integer openIssues,
        @JsonProperty("closed_issues") Integer closedIssues
) {}
