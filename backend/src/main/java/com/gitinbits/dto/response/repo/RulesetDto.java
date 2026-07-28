package com.gitinbits.dto.response.repo;

public record RulesetDto(
        Long id,
        String name,
        String target,
        String enforcement,
        Object conditions,
        java.util.List<Object> rules
) {}
