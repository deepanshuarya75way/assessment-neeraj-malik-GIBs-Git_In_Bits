package com.gitinbits.client.github.raw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RawRuleset(
        @JsonProperty("id") Long id,
        @JsonProperty("name") String name,
        @JsonProperty("target") String target,
        @JsonProperty("enforcement") String enforcement,
        @JsonProperty("conditions") JsonNode conditions,
        @JsonProperty("rules") java.util.List<JsonNode> rules
) {}
