package com.gitinbits.client.github.raw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RawLabel(
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("color") String color
) {}
