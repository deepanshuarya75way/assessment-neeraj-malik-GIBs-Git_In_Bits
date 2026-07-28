package com.gitinbits.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/metadata-dictionary")
public class MetadataDictionaryController {

    @GetMapping
    public ResponseEntity<List<Map<String, String>>> getDictionary() {
        return ResponseEntity.ok(List.of(
                Map.of("field", "rulesets", "description", "GitHub repository rulesets", "futureUsage", "Assess repository compliance and guardrails."),
                Map.of("field", "workflows", "description", "GitHub Actions workflows", "futureUsage", "Analyze CI/CD setup and automation footprint."),
                Map.of("field", "deployments", "description", "GitHub deployments", "futureUsage", "Calculate DORA metrics like deployment frequency.")
        ));
    }
}
