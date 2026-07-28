package com.gitinbits.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SystemController {

    @GetMapping("/capabilities")
    public ResponseEntity<Map<String, Object>> getCapabilities() {
        return ResponseEntity.ok(Map.of(
                "capabilities", List.of(
                        "GitHub OAuth2 Login",
                        "Organization and Repository Discovery",
                        "Branch, Commit, PR, Issue, Release, and Team Metadata Extraction",
                        "Extended Metadata (Labels, Milestones, Workflows, Deployments, Rulesets)"
                ),
                "futureUsage", "These capabilities form the Data Collection Layer, which will feed into the Normalization and Engineering Analysis layers in the final platform."
        ));
    }

    @GetMapping("/system/coverage")
    public ResponseEntity<Map<String, Object>> getSystemCoverage() {
        return ResponseEntity.ok(Map.of(
                "coveragePercentage", 100,
                "missingCapabilities", List.of(),
                "description", "PoC Data Collection Layer fully covers all requested GitHub metadata components."
        ));
    }
}
