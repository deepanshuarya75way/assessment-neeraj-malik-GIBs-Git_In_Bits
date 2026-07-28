package com.gitinbits.controller;

import com.gitinbits.dto.context.GitHubContext;
import com.gitinbits.dto.response.repo.*;
import com.gitinbits.service.github.GitHubRepoService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles all repository-related endpoints.
 */
@Validated
@RestController
@RequestMapping("/api/repos")
public class RepoController {

    private static final Logger log = LoggerFactory.getLogger(RepoController.class);

    private final GitHubRepoService repoService;
    private final com.gitinbits.service.github.GitHubMetadataService metadataService;

    public RepoController(GitHubRepoService repoService, com.gitinbits.service.github.GitHubMetadataService metadataService) {
        this.repoService = repoService;
        this.metadataService = metadataService;
    }

    @GetMapping
    public ResponseEntity<List<RepoDto>> listRepos(
            GitHubContext context) {
        log.debug("GET /api/repos [accountName={}]", context.accountName());
        return ResponseEntity.ok(repoService.listRepos(context));
    }

    @GetMapping("/{repo}")
    public ResponseEntity<RepoDto> getRepo(
            GitHubContext context,
            @PathVariable @NotBlank String repo) {
        log.debug("GET /api/repos/{} [accountName={}]", repo, context.accountName());
        return ResponseEntity.ok(repoService.getRepo(context, repo));
    }

    @GetMapping("/{repo}/branches")
    public ResponseEntity<List<BranchDto>> listBranches(
            GitHubContext context,
            @PathVariable @NotBlank String repo) {
        log.debug("GET /api/repos/{}/branches [accountName={}]", repo, context.accountName());
        return ResponseEntity.ok(repoService.listBranches(context, repo));
    }

    @GetMapping("/{repo}/commits")
    public ResponseEntity<List<CommitDto>> listCommits(
            GitHubContext context,
            @PathVariable @NotBlank String repo) {
        log.debug("GET /api/repos/{}/commits [accountName={}]", repo, context.accountName());
        return ResponseEntity.ok(repoService.listCommits(context, repo));
    }

    @GetMapping("/{repo}/pulls")
    public ResponseEntity<List<PullRequestDto>> listPulls(
            GitHubContext context,
            @PathVariable @NotBlank String repo) {
        log.debug("GET /api/repos/{}/pulls [accountName={}]", repo, context.accountName());
        return ResponseEntity.ok(repoService.listPulls(context, repo));
    }

    @GetMapping("/{repo}/pulls/{number}/reviews")
    public ResponseEntity<List<ReviewDto>> listReviews(
            GitHubContext context,
            @PathVariable @NotBlank String repo,
            @PathVariable @Positive int number) {
        log.debug("GET /api/repos/{}/pulls/{}/reviews [accountName={}]", repo, number, context.accountName());
        return ResponseEntity.ok(repoService.listReviews(context, repo, number));
    }

    @GetMapping("/{repo}/pulls/{number}/comments")
    public ResponseEntity<List<ReviewCommentDto>> listReviewComments(
            GitHubContext context,
            @PathVariable @NotBlank String repo,
            @PathVariable @Positive int number) {
        log.debug("GET /api/repos/{}/pulls/{}/comments [accountName={}]", repo, number, context.accountName());
        return ResponseEntity.ok(repoService.listReviewComments(context, repo, number));
    }

    @GetMapping("/{repo}/issues")
    public ResponseEntity<List<IssueDto>> listIssues(
            GitHubContext context,
            @PathVariable @NotBlank String repo) {
        log.debug("GET /api/repos/{}/issues [accountName={}]", repo, context.accountName());
        return ResponseEntity.ok(repoService.listIssues(context, repo));
    }

    @GetMapping("/{repo}/contributors")
    public ResponseEntity<List<ContributorDto>> listContributors(
            GitHubContext context,
            @PathVariable @NotBlank String repo) {
        log.debug("GET /api/repos/{}/contributors [accountName={}]", repo, context.accountName());
        return ResponseEntity.ok(repoService.listContributors(context, repo));
    }

    @GetMapping("/{repo}/releases")
    public ResponseEntity<List<ReleaseDto>> listReleases(
            GitHubContext context,
            @PathVariable @NotBlank String repo) {
        log.debug("GET /api/repos/{}/releases [accountName={}]", repo, context.accountName());
        return ResponseEntity.ok(repoService.listReleases(context, repo));
    }

    // ─── Metadata Service Endpoints ──────────────────────────────────────────

    @GetMapping("/{repo}/commits/comments")
    public ResponseEntity<List<CommitCommentDto>> listCommitComments(
            GitHubContext context,
            @PathVariable @NotBlank String repo) {
        return ResponseEntity.ok(metadataService.listCommitComments(context, repo));
    }

    @GetMapping("/{repo}/issues/comments")
    public ResponseEntity<List<IssueCommentDto>> listIssueComments(
            GitHubContext context,
            @PathVariable @NotBlank String repo) {
        return ResponseEntity.ok(metadataService.listIssueComments(context, repo));
    }

    @GetMapping("/{repo}/labels")
    public ResponseEntity<List<LabelDto>> listLabels(
            GitHubContext context,
            @PathVariable @NotBlank String repo) {
        return ResponseEntity.ok(metadataService.listLabels(context, repo));
    }

    @GetMapping("/{repo}/milestones")
    public ResponseEntity<List<MilestoneDto>> listMilestones(
            GitHubContext context,
            @PathVariable @NotBlank String repo) {
        return ResponseEntity.ok(metadataService.listMilestones(context, repo));
    }

    @GetMapping("/{repo}/actions/workflows")
    public ResponseEntity<List<WorkflowDto>> listWorkflows(
            GitHubContext context,
            @PathVariable @NotBlank String repo) {
        return ResponseEntity.ok(metadataService.listWorkflows(context, repo));
    }

    @GetMapping("/{repo}/actions/runs")
    public ResponseEntity<List<WorkflowRunDto>> listWorkflowRuns(
            GitHubContext context,
            @PathVariable @NotBlank String repo) {
        return ResponseEntity.ok(metadataService.listWorkflowRuns(context, repo));
    }

    @GetMapping("/{repo}/deployments")
    public ResponseEntity<List<DeploymentDto>> listDeployments(
            GitHubContext context,
            @PathVariable @NotBlank String repo) {
        return ResponseEntity.ok(metadataService.listDeployments(context, repo));
    }

    @GetMapping("/{repo}/collaborators")
    public ResponseEntity<List<CollaboratorDto>> listCollaborators(
            GitHubContext context,
            @PathVariable @NotBlank String repo) {
        return ResponseEntity.ok(metadataService.listCollaborators(context, repo));
    }

    @GetMapping("/{repo}/rulesets")
    public ResponseEntity<List<RulesetDto>> listRulesets(
            GitHubContext context,
            @PathVariable @NotBlank String repo) {
        return ResponseEntity.ok(metadataService.listRulesets(context, repo));
    }
}
