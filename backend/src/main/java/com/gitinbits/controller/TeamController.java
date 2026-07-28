package com.gitinbits.controller;

import com.gitinbits.dto.context.GitHubContext;
import com.gitinbits.dto.response.team.TeamDto;
import com.gitinbits.service.github.GitHubTeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles team metadata endpoints.
 */
@Validated
@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private static final Logger log = LoggerFactory.getLogger(TeamController.class);

    private final GitHubTeamService teamService;

    public TeamController(GitHubTeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping
    public ResponseEntity<List<TeamDto>> listTeams(GitHubContext context) {
        log.debug("GET /api/teams [accountName={}]", context.accountName());
        return ResponseEntity.ok(teamService.listTeams(context));
    }
}
