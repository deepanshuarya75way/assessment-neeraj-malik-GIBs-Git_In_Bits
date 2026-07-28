package com.gitinbits.controller;

import com.gitinbits.dto.context.GitHubContext;
import com.gitinbits.dto.response.org.OrgDto;
import com.gitinbits.service.github.GitHubOrgService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Handles organization metadata endpoints.
 */
@Validated
@RestController
@RequestMapping("/api/org")
public class OrgController {

    private static final Logger log = LoggerFactory.getLogger(OrgController.class);

    private final GitHubOrgService orgService;

    public OrgController(GitHubOrgService orgService) {
        this.orgService = orgService;
    }

    @GetMapping
    public ResponseEntity<OrgDto> getOrg(GitHubContext context) {
        log.debug("GET /api/org [accountName={}]", context.accountName());
        return ResponseEntity.ok(orgService.getOrg(context));
    }
}
