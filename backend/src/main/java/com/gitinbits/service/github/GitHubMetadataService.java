package com.gitinbits.service.github;

import com.gitinbits.client.github.GitHubClient;
import com.gitinbits.client.github.raw.*;
import com.gitinbits.dto.context.GitHubContext;
import com.gitinbits.dto.response.repo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GitHubMetadataService {
    private static final Logger log = LoggerFactory.getLogger(GitHubMetadataService.class);
    private final GitHubClient gitHubClient;

    public GitHubMetadataService(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }

    public List<CommitCommentDto> listCommitComments(GitHubContext context, String repo) {
        return gitHubClient.listCommitComments(context.accountName(), repo)
                .stream()
                .map(this::toCommitCommentDto)
                .toList();
    }

    public List<IssueCommentDto> listIssueComments(GitHubContext context, String repo) {
        return gitHubClient.listIssueComments(context.accountName(), repo)
                .stream()
                .map(this::toIssueCommentDto)
                .toList();
    }

    public List<LabelDto> listLabels(GitHubContext context, String repo) {
        return gitHubClient.listLabels(context.accountName(), repo)
                .stream()
                .map(this::toLabelDto)
                .toList();
    }

    public List<MilestoneDto> listMilestones(GitHubContext context, String repo) {
        return gitHubClient.listMilestones(context.accountName(), repo)
                .stream()
                .map(this::toMilestoneDto)
                .toList();
    }

    public List<WorkflowDto> listWorkflows(GitHubContext context, String repo) {
        return gitHubClient.listWorkflows(context.accountName(), repo)
                .stream()
                .map(this::toWorkflowDto)
                .toList();
    }

    public List<WorkflowRunDto> listWorkflowRuns(GitHubContext context, String repo) {
        return gitHubClient.listWorkflowRuns(context.accountName(), repo)
                .stream()
                .map(this::toWorkflowRunDto)
                .toList();
    }

    public List<DeploymentDto> listDeployments(GitHubContext context, String repo) {
        return gitHubClient.listDeployments(context.accountName(), repo)
                .stream()
                .map(this::toDeploymentDto)
                .toList();
    }

    public List<CollaboratorDto> listCollaborators(GitHubContext context, String repo) {
        return gitHubClient.listCollaborators(context.accountName(), repo)
                .stream()
                .map(this::toCollaboratorDto)
                .toList();
    }

    public List<RulesetDto> listRulesets(GitHubContext context, String repo) {
        return gitHubClient.listRulesets(context.accountName(), repo)
                .stream()
                .map(this::toRulesetDto)
                .toList();
    }

    private CommitCommentDto toCommitCommentDto(RawCommitComment r) {
        return new CommitCommentDto(r.id(), r.user() != null ? r.user().login() : null, r.body(), r.createdAt(), r.updatedAt());
    }

    private IssueCommentDto toIssueCommentDto(RawIssueComment r) {
        return new IssueCommentDto(r.id(), r.user() != null ? r.user().login() : null, r.body(), r.createdAt(), r.updatedAt());
    }

    private LabelDto toLabelDto(RawLabel r) {
        return new LabelDto(r.name(), r.description(), r.color());
    }

    private MilestoneDto toMilestoneDto(RawMilestone r) {
        return new MilestoneDto(r.title(), r.description(), r.dueOn(), r.state(), r.openIssues(), r.closedIssues());
    }

    private WorkflowDto toWorkflowDto(RawWorkflow r) {
        return new WorkflowDto(r.name(), r.state(), r.path(), r.createdAt(), r.updatedAt());
    }

    private WorkflowRunDto toWorkflowRunDto(RawWorkflowRun r) {
        return new WorkflowRunDto(r.name(), r.headBranch(), r.status(), r.conclusion(), r.event(), r.createdAt(), r.updatedAt(), r.actor() != null ? r.actor().login() : null);
    }

    private DeploymentDto toDeploymentDto(RawDeployment r) {
        return new DeploymentDto(r.id(), r.environment(), null, r.creator() != null ? r.creator().login() : null, r.createdAt(), r.updatedAt(), r.repositoryUrl());
    }

    private CollaboratorDto toCollaboratorDto(RawCollaborator r) {
        CollaboratorDto.Permissions permissions = r.permissions() != null ? new CollaboratorDto.Permissions(
                r.permissions().admin(),
                r.permissions().maintain(),
                r.permissions().push(),
                r.permissions().triage(),
                r.permissions().pull()
        ) : null;
        return new CollaboratorDto(r.login(), r.avatarUrl(), r.htmlUrl(), r.roleName(), permissions);
    }

    private RulesetDto toRulesetDto(RawRuleset r) {
        java.util.List<Object> rules = r.rules() != null ? r.rules().stream().map(node -> (Object) node).toList() : null;
        return new RulesetDto(r.id(), r.name(), r.target(), r.enforcement(), r.conditions(), rules);
    }
}
