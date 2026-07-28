package com.gitinbits.controller;

import com.gitinbits.dto.response.auth.AuthUserDto;
import com.gitinbits.dto.response.org.OrgSummaryDto;
import com.gitinbits.service.github.GitHubAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles authentication-related endpoints.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final GitHubAuthService authService;

    public AuthController(GitHubAuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/me")
    public ResponseEntity<AuthUserDto> me() {
        log.debug("GET /api/auth/me");
        return ResponseEntity.ok(authService.getAuthenticatedUser());
    }

    @GetMapping("/orgs")
    public ResponseEntity<List<OrgSummaryDto>> userOrgs() {
        log.debug("GET /api/auth/orgs");
        return ResponseEntity.ok(authService.getUserOrgs());
    }
}
