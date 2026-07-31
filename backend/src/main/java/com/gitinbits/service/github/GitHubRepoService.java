package com.gitinbits.service.github;

import com.gitinbits.client.github.GitHubClient;
import com.gitinbits.client.github.raw.*;
import com.gitinbits.dto.context.DataSourceType;
import com.gitinbits.dto.context.GitHubContext;
import com.gitinbits.dto.response.repo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Service for GitHub repository and sub-resource metadata.
 */
@Service
public class GitHubRepoService {

    private static final Logger log = LoggerFactory.getLogger(GitHubRepoService.class);

    private final GitHubClient gitHubClient;

    public GitHubRepoService(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }

    // ─── Repositories ─────────────────────────────────────────────────────────

    @Cacheable("repos")
    public List<RepoDto> listRepos(GitHubContext context) {
        log.debug("Listing repositories for account: {}", context.accountName());
        if (context.sourceType() == DataSourceType.PUBLIC_REPOSITORY) {
            return List.of(toRepoDto(gitHubClient.getRepository(context.accountName(), context.repositoryName())));
        }
        return gitHubClient.listRepositories(context.accountName())
                .stream()
                .map(this::toRepoDto)
                .toList();
    }

    @Cacheable("repo")
    public RepoDto getRepo(GitHubContext context, String repo) {
        log.debug("Fetching repo details: {}/{}", context.accountName(), repo);
        return toRepoDto(gitHubClient.getRepository(context.accountName(), repo));
    }

    // ─── Branches ─────────────────────────────────────────────────────────────

    @Cacheable("branches")
    public List<BranchDto> listBranches(GitHubContext context, String repo) {
        log.debug("Listing branches for: {}/{}", context.accountName(), repo);
        return gitHubClient.listBranches(context.accountName(), repo)
                .stream()
                .map(this::toBranchDto)
                .toList();
    }

    // ─── Commits ──────────────────────────────────────────────────────────────

    @Cacheable("commits")
    public List<CommitDto> listCommits(GitHubContext context, String repo) {
        log.debug("Listing commits for: {}/{}", context.accountName(), repo);
        return gitHubClient.listCommits(context.accountName(), repo)
                .stream()
                .map(this::toCommitDto)
                .toList();
    }

    public CommitDto getCommit(GitHubContext context, String repo, String sha) {
        log.debug("Fetching detailed commit stats for: {}/{} sha: {}", context.accountName(), repo, sha);
        return toCommitDto(gitHubClient.getCommit(context.accountName(), repo, sha));
    }

    // ─── Pull Requests ────────────────────────────────────────────────────────

    public List<PullRequestDto> listPulls(GitHubContext context, String repo) {
        log.debug("Listing pull requests for: {}/{}", context.accountName(), repo);
        return gitHubClient.listPullRequests(context.accountName(), repo)
                .stream()
                .map(this::toPullRequestDto)
                .toList();
    }

    public List<ReviewDto> listReviews(GitHubContext context, String repo, int prNumber) {
        log.debug("Listing reviews for: {}/{} PR#{}", context.accountName(), repo, prNumber);
        return gitHubClient.listReviews(context.accountName(), repo, prNumber)
                .stream()
                .map(this::toReviewDto)
                .toList();
    }

    public List<ReviewCommentDto> listReviewComments(GitHubContext context, String repo, int prNumber) {
        log.debug("Listing review comments for: {}/{} PR#{}", context.accountName(), repo, prNumber);
        return gitHubClient.listReviewComments(context.accountName(), repo, prNumber)
                .stream()
                .map(this::toReviewCommentDto)
                .toList();
    }

    // ─── Issues ───────────────────────────────────────────────────────────────

    public List<IssueDto> listIssues(GitHubContext context, String repo) {
        log.debug("Listing issues for: {}/{}", context.accountName(), repo);
        return gitHubClient.listIssues(context.accountName(), repo)
                .stream()
                .filter(issue -> !issue.isPullRequest())
                .map(this::toIssueDto)
                .toList();
    }

    // ─── Contributors ─────────────────────────────────────────────────────────

    @Cacheable("contributors")
    public List<ContributorDto> listContributors(GitHubContext context, String repo) {
        log.debug("Listing contributors for: {}/{}", context.accountName(), repo);
        return gitHubClient.listContributors(context.accountName(), repo)
                .stream()
                .map(this::toContributorDto)
                .toList();
    }

    // ─── Releases ─────────────────────────────────────────────────────────────

    public List<ReleaseDto> listReleases(GitHubContext context, String repo) {
        log.debug("Listing releases for: {}/{}", context.accountName(), repo);
        return gitHubClient.listReleases(context.accountName(), repo)
                .stream()
                .map(this::toReleaseDto)
                .toList();
    }

    public List<WorkflowRunDto> listWorkflowRuns(GitHubContext context, String repo) {
        log.debug("Listing workflow runs for: {}/{}", context.accountName(), repo);
        return gitHubClient.listWorkflowRuns(context.accountName(), repo)
                .stream()
                .map(this::toWorkflowRunDto)
                .toList();
    }

    // ─── Mappers ──────────────────────────────────────────────────────────────

    private RepoDto toRepoDto(RawRepo r) {
        return new RepoDto(
                r.name(),
                r.fullName(),
                r.description(),
                r.visibility(),
                r.defaultBranch(),
                r.language(),
                r.htmlUrl(),
                r.cloneUrl(),
                r.sshUrl(),
                r.fork(),
                r.archived(),
                r.topics() != null ? r.topics() : List.of(),
                r.openIssuesCount(),
                r.forksCount(),
                r.stargazersCount(),
                r.watchersCount(),
                r.size(),
                r.createdAt(),
                r.updatedAt(),
                r.pushedAt(),
                r.homepage(),
                r.networkCount(),
                r.subscribersCount(),
                r.license() != null ? r.license().name() : null
        );
    }

    private BranchDto toBranchDto(RawBranch r) {
        String sha = r.commit() != null ? r.commit().sha() : null;
        Boolean reqStatus = r.protection() != null && r.protection().requiredStatusChecks() != null ? r.protection().requiredStatusChecks().strict() : null;
        Boolean reqReviews = r.protection() != null && r.protection().requiredPullRequestReviews() != null ? r.protection().requiredPullRequestReviews().dismissStaleReviews() : null;
        Boolean forcePush = r.protection() != null && r.protection().allowForcePushes() != null ? r.protection().allowForcePushes().enabled() : null;
        Boolean allowDeletion = r.protection() != null && r.protection().allowDeletions() != null ? r.protection().allowDeletions().enabled() : null;
        return new BranchDto(r.name(), r.isProtected(), sha, null, null, reqStatus, reqReviews, forcePush, allowDeletion);
    }

    private CommitDto toCommitDto(RawCommit r) {
        RawCommit.CommitData data = r.commit();
        String authorName     = data != null && data.author()    != null ? data.author().name()    : null;
        String authorEmail    = data != null && data.author()    != null ? data.author().email()   : null;
        String committerName  = data != null && data.committer() != null ? data.committer().name()  : null;
        String committerEmail = data != null && data.committer() != null ? data.committer().email() : null;
        String message        = data != null ? data.message() : null;
        String timestamp      = data != null && data.author() != null ? data.author().date() : null;

        List<String> parents = r.parents() != null 
                ? r.parents().stream().map(RawCommit.Parent::sha).toList()
                : List.of();
        Boolean verified = data != null && data.verification() != null ? data.verification().verified() : null;
        String vReason = data != null && data.verification() != null ? data.verification().reason() : null;
        Integer additions = r.stats() != null ? r.stats().additions() : null;
        Integer deletions = r.stats() != null ? r.stats().deletions() : null;
        Integer total = r.stats() != null ? r.stats().total() : null;
        Integer filesCount = r.files() != null ? r.files().size() : null;
        List<String> fileNames = r.files() != null 
                ? r.files().stream().map(RawCommit.File::filename).toList()
                : List.of();

        return new CommitDto(
                r.sha(),
                authorName,
                authorEmail,
                committerName,
                committerEmail,
                message,
                timestamp,
                r.htmlUrl(),
                parents,
                verified,
                vReason,
                filesCount,
                additions,
                deletions,
                total,
                fileNames
        );
    }

    private PullRequestDto toPullRequestDto(RawPullRequest r) {
        List<String> assignees = r.assignees() != null 
                ? r.assignees().stream().map(RawPullRequest.User::login).toList() 
                : List.of();
        List<String> reviewers = r.requestedReviewers() != null 
                ? r.requestedReviewers().stream().map(RawPullRequest.User::login).toList() 
                : List.of();
        List<String> labels = r.labels() != null 
                ? r.labels().stream().map(RawPullRequest.Label::name).toList() 
                : List.of();
        String milestone = r.milestone() != null ? r.milestone().title() : null;

        return new PullRequestDto(
                r.number(),
                r.title(),
                r.body(),
                r.user() != null ? r.user().login() : null,
                r.state(),
                r.locked(),
                r.draft(),
                r.createdAt(),
                r.updatedAt(),
                r.closedAt(),
                r.mergedAt(),
                r.mergeCommitSha(),
                r.mergeableState(),
                assignees,
                reviewers,
                labels,
                milestone,
                r.commits(),
                r.changedFiles(),
                r.additions(),
                r.deletions(),
                r.reviewComments(),
                r.comments(),
                r.base() != null ? r.base().ref() : null,
                r.head() != null ? r.head().ref() : null
        );
    }

    private ReviewDto toReviewDto(RawReview r) {
        return new ReviewDto(
                r.user() != null ? r.user().login() : null,
                r.state(),
                r.submittedAt(),
                r.body(),
                r.commitId()
        );
    }

    private ReviewCommentDto toReviewCommentDto(RawReviewComment r) {
        return new ReviewCommentDto(
                r.id(),
                r.user() != null ? r.user().login() : null,
                r.body(),
                r.path(),
                r.line(),
                r.originalLine(),
                r.createdAt(),
                r.updatedAt()
        );
    }

    private IssueDto toIssueDto(RawIssue r) {
        List<String> labels = r.labels() != null
                ? r.labels().stream().map(RawIssue.Label::name).filter(Objects::nonNull).toList()
                : List.of();
        List<String> assignees = r.assignees() != null
                ? r.assignees().stream().map(RawIssue.Assignee::login).filter(Objects::nonNull).toList()
                : List.of();
        String milestone = r.milestone() != null ? r.milestone().title() : null;
        return new IssueDto(
                r.number(),
                r.title(),
                r.body(),
                labels,
                assignees,
                milestone,
                r.state(),
                r.locked(),
                r.comments(),
                r.createdAt(),
                r.updatedAt(),
                r.closedAt()
        );
    }

    private ContributorDto toContributorDto(RawContributor r) {
        return new ContributorDto(r.login(), r.avatarUrl(), r.htmlUrl(), r.contributions());
    }

    private ReleaseDto toReleaseDto(RawRelease r) {
        return new ReleaseDto(
                r.tagName(),
                r.tagName(),
                r.name(),
                r.body(),
                r.draft(),
                r.prerelease(),
                r.publishedAt(),
                r.author() != null ? r.author().login() : null
        );
    }

    private WorkflowRunDto toWorkflowRunDto(RawWorkflowRun r) {
        return new WorkflowRunDto(
                r.name(),
                r.headBranch(),
                r.status(),
                r.conclusion(),
                r.event(),
                r.createdAt(),
                r.updatedAt(),
                r.actor() != null ? r.actor().login() : null
        );
    }
}
