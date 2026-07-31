package com.gitinbits.client.github;

import com.gitinbits.client.github.raw.*;

import java.util.List;

/**
 * Contract for all GitHub REST API interactions.
 *
 * <p>This interface is the sole gateway between the service layer and the GitHub API.
 * No service, controller, or other component may call GitHub directly.
 *
 * <p><b>Design Notes:</b>
 * <ul>
 *   <li>Methods return internal {@code Raw*} types. Services are responsible
 *       for mapping these to public DTOs, keeping the client layer thin.</li>
 *   <li>Token retrieval is an implementation detail — implementations obtain
 *       the OAuth2 access token from the security context transparently.</li>
 *   <li>Pagination is fully transparent — list methods always return complete datasets.</li>
 *   <li>If a future provider (GitLab, Bitbucket) is added, a new interface can be
 *       introduced, and the service layer updated to select the correct client.</li>
 * </ul>
 */
public interface GitHubClient {

    // ─── Authenticated User ───────────────────────────────────────────────────

    /** GET /user — authenticated user profile */
    RawUser getCurrentUser();

    /** GET /user/orgs — organizations the authenticated user belongs to */
    List<RawOrg> getUserOrgs();

    // ─── Organization ─────────────────────────────────────────────────────────

    /** GET /orgs/{org} — full organization details */
    RawOrg getOrganization(String org);

    // ─── Repositories ─────────────────────────────────────────────────────────

    /** GET /orgs/{org}/repos — all repositories in the organization */
    List<RawRepo> listRepositories(String org);

    /** GET /repos/{org}/{repo} — single repository detail */
    RawRepo getRepository(String org, String repo);

    // ─── Branches ─────────────────────────────────────────────────────────────

    /** GET /repos/{org}/{repo}/branches — all branches */
    List<RawBranch> listBranches(String org, String repo);

    // ─── Commits ──────────────────────────────────────────────────────────────

    /** GET /repos/{org}/{repo}/commits — all commits on the default branch */
    List<RawCommit> listCommits(String org, String repo);

    /** GET /repos/{org}/{repo}/commits/{sha} — single commit with stats */
    RawCommit getCommit(String org, String repo, String sha);

    // ─── Pull Requests ────────────────────────────────────────────────────────

    /** GET /repos/{org}/{repo}/pulls?state=all — all pull requests */
    List<RawPullRequest> listPullRequests(String org, String repo);

    /** GET /repos/{org}/{repo}/pulls/{prNumber}/reviews — all reviews for a PR */
    List<RawReview> listReviews(String org, String repo, int prNumber);

    /** GET /repos/{org}/{repo}/pulls/{prNumber}/comments — all inline review comments */
    List<RawReviewComment> listReviewComments(String org, String repo, int prNumber);

    // ─── Issues ───────────────────────────────────────────────────────────────

    /** GET /repos/{org}/{repo}/issues?state=all — all issues (PRs filtered server-side) */
    List<RawIssue> listIssues(String org, String repo);

    // ─── Contributors ─────────────────────────────────────────────────────────

    /** GET /repos/{org}/{repo}/contributors — all contributors */
    List<RawContributor> listContributors(String org, String repo);

    // ─── Releases ─────────────────────────────────────────────────────────────

    /** GET /repos/{org}/{repo}/releases — all releases */
    List<RawRelease> listReleases(String org, String repo);

    // ─── Teams ────────────────────────────────────────────────────────────────

    /** GET /orgs/{org}/teams — all teams in the organization */
    List<RawTeam> listTeams(String org);

    /** GET /orgs/{org}/teams/{teamSlug}/members — all members of a team */
    List<RawMember> listTeamMembers(String org, String teamSlug);

    /** GET /orgs/{org}/teams/{teamSlug}/repos — all repositories a team has access to */
    List<RawTeamRepo> listTeamRepos(String org, String teamSlug);

    // ─── Metadata ─────────────────────────────────────────────────────────────

    /** GET /repos/{org}/{repo}/comments — all commit comments */
    List<RawCommitComment> listCommitComments(String org, String repo);

    /** GET /repos/{org}/{repo}/issues/comments — all issue comments */
    List<RawIssueComment> listIssueComments(String org, String repo);

    /** GET /repos/{org}/{repo}/labels — all labels */
    List<RawLabel> listLabels(String org, String repo);

    /** GET /repos/{org}/{repo}/milestones?state=all — all milestones */
    List<RawMilestone> listMilestones(String org, String repo);

    /** GET /repos/{org}/{repo}/actions/workflows — all GitHub Actions workflows */
    List<RawWorkflow> listWorkflows(String org, String repo);

    /** GET /repos/{org}/{repo}/actions/runs — all GitHub Actions workflow runs */
    List<RawWorkflowRun> listWorkflowRuns(String org, String repo);

    /** GET /repos/{org}/{repo}/deployments — all deployments */
    List<RawDeployment> listDeployments(String org, String repo);

    /** GET /repos/{org}/{repo}/collaborators — all collaborators */
    List<RawCollaborator> listCollaborators(String org, String repo);

    /** GET /repos/{org}/{repo}/rulesets — all rulesets */
    List<RawRuleset> listRulesets(String org, String repo);
}
