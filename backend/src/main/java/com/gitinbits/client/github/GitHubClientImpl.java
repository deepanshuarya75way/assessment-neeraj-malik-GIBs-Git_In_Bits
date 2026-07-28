package com.gitinbits.client.github;

import com.gitinbits.client.github.pagination.GitHubPaginator;
import com.gitinbits.client.github.raw.*;
import com.gitinbits.config.GitHubProperties;
import com.gitinbits.exception.GitHubApiException;
import com.gitinbits.exception.GitHubAuthException;
import com.gitinbits.exception.GitHubNotFoundException;
import com.gitinbits.exception.GitHubRateLimitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.List;

/**
 * RestClient-based implementation of {@link GitHubClient}.
 *
 * <p><b>Token strategy</b>: The OAuth2 access token is retrieved from the
 * {@link OAuth2AuthorizedClientService} (in-memory store) using the current
 * security context on every call. This means no token needs to be passed
 * through the service layer — the client is fully self-contained.
 *
 * <p><b>Pagination</b>: All list operations delegate to {@link GitHubPaginator},
 * which transparently follows GitHub's {@code Link} response headers until
 * all pages are exhausted.
 *
 * <p><b>Error mapping</b>:
 * <ul>
 *   <li>401 → {@link GitHubAuthException}</li>
 *   <li>403 + rate-limit headers → {@link GitHubRateLimitException}</li>
 *   <li>403 (other) → {@link GitHubAuthException}</li>
 *   <li>404 → {@link GitHubNotFoundException}</li>
 *   <li>5xx / other → {@link GitHubApiException}</li>
 * </ul>
 */
@Component
public class GitHubClientImpl implements GitHubClient {

    private static final Logger log = LoggerFactory.getLogger(GitHubClientImpl.class);

    private final RestClient gitHubRestClient;
    private final GitHubPaginator paginator;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final GitHubProperties properties;

    public GitHubClientImpl(RestClient gitHubRestClient,
                            GitHubPaginator paginator,
                            OAuth2AuthorizedClientService authorizedClientService,
                            GitHubProperties properties) {
        this.gitHubRestClient = gitHubRestClient;
        this.paginator = paginator;
        this.authorizedClientService = authorizedClientService;
        this.properties = properties;
    }

    // ─── Token Resolution ─────────────────────────────────────────────────────

    /**
     * Retrieves the current user's GitHub OAuth2 access token from the in-memory store.
     *
     * @return bearer token string
     * @throws GitHubAuthException if no valid OAuth2 session exists
     */
    private String getAccessToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof OAuth2AuthenticationToken oauthToken)) {
            throw new GitHubAuthException(
                    "No active GitHub OAuth2 session. Please log in via /oauth2/authorization/github");
        }

        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(),
                oauthToken.getName()
        );

        if (client == null || client.getAccessToken() == null) {
            throw new GitHubAuthException(
                    "GitHub access token not found in session. Please re-authenticate.");
        }

        return client.getAccessToken().getTokenValue();
    }

    // ─── Internal Helpers ─────────────────────────────────────────────────────

    /**
     * Fetches a single GitHub resource object.
     */
    private <T> T fetchSingle(String path, Class<T> responseType) {
        String token = getAccessToken();
        URI uri = URI.create(properties.getBaseUrl() + path);
        log.debug("→ GET {}", uri);

        try {
            T result = gitHubRestClient.get()
                    .uri(uri)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .body(responseType);
            log.debug("← 200 OK {}", uri);
            return result;
        } catch (HttpClientErrorException e) {
            throw mapClientError(e, path);
        } catch (HttpServerErrorException e) {
            throw new GitHubApiException(
                    "GitHub server error " + e.getStatusCode().value() + " for: " + path, e);
        }
    }

    /**
     * Fetches all pages of a GitHub list endpoint using transparent pagination.
     */
    private <T> List<T> fetchList(String path, ParameterizedTypeReference<List<T>> responseType) {
        String token = getAccessToken();
        URI initialUri = URI.create(properties.getBaseUrl() + path);
        log.debug("→ GET {} (paginated)", initialUri);

        try {
            List<T> result = paginator.fetchAll(gitHubRestClient, initialUri, token, responseType);
            log.debug("← Completed {} — {} total items", initialUri, result.size());
            return result;
        } catch (HttpClientErrorException e) {
            throw mapClientError(e, path);
        } catch (HttpServerErrorException e) {
            throw new GitHubApiException(
                    "GitHub server error " + e.getStatusCode().value() + " for: " + path, e);
        }
    }

    /**
     * Maps Spring's {@link HttpClientErrorException} to a domain-specific exception.
     */
    private RuntimeException mapClientError(HttpClientErrorException e, String context) {
        int code = e.getStatusCode().value();
        HttpHeaders responseHeaders = e.getResponseHeaders();

        if (code == HttpStatus.UNAUTHORIZED.value()) {
            return new GitHubAuthException("GitHub authentication failed for: " + context
                    + ". Your OAuth token may have been revoked.");
        }

        if (code == HttpStatus.FORBIDDEN.value()) {
            String remaining = responseHeaders != null
                    ? responseHeaders.getFirst("X-RateLimit-Remaining") : null;
            if ("0".equals(remaining)) {
                String reset = responseHeaders.getFirst("X-RateLimit-Reset");
                long resetEpoch = reset != null ? parseLongSafe(reset) : 0L;
                return new GitHubRateLimitException(
                        "GitHub API rate limit exceeded. Resets at epoch: " + resetEpoch,
                        resetEpoch);
            }
            return new com.gitinbits.exception.GitHubForbiddenException(
                    "GitHub access denied for: " + context
                    + ". Check that your OAuth App has the required scopes (read:org, repo).");
        }

        if (code == HttpStatus.NOT_FOUND.value()) {
            return new GitHubNotFoundException(context);
        }

        return new GitHubApiException(
                "GitHub API returned HTTP " + code + " for: " + context
                + " — " + e.getMessage(), e);
    }

    private long parseLongSafe(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ignored) {
            return 0L;
        }
    }

    // ─── GitHubClient Implementation ──────────────────────────────────────────

    @Override
    public RawUser getCurrentUser() {
        return fetchSingle("/user", RawUser.class);
    }

    @Override
    public List<RawOrg> getUserOrgs() {
        return fetchList("/user/orgs", new ParameterizedTypeReference<>() {});
    }

    @Override
    public RawOrg getOrganization(String org) {
        return fetchSingle("/orgs/" + org, RawOrg.class);
    }

    @Override
    public List<RawRepo> listRepositories(String org) {
        try {
            return fetchList("/orgs/" + org + "/repos?type=all", new ParameterizedTypeReference<>() {});
        } catch (GitHubNotFoundException e) {
            // Fallback for personal user accounts
            return fetchList("/users/" + org + "/repos?type=all", new ParameterizedTypeReference<>() {});
        }
    }

    @Override
    public RawRepo getRepository(String org, String repo) {
        return fetchSingle("/repos/" + org + "/" + repo, RawRepo.class);
    }

    @Override
    public List<RawBranch> listBranches(String org, String repo) {
        return fetchList("/repos/" + org + "/" + repo + "/branches", new ParameterizedTypeReference<>() {});
    }

    @Override
    public List<RawCommit> listCommits(String org, String repo) {
        return fetchList("/repos/" + org + "/" + repo + "/commits", new ParameterizedTypeReference<>() {});
    }

    @Override
    public List<RawPullRequest> listPullRequests(String org, String repo) {
        return fetchList("/repos/" + org + "/" + repo + "/pulls?state=all", new ParameterizedTypeReference<>() {});
    }

    @Override
    public List<RawReview> listReviews(String org, String repo, int prNumber) {
        return fetchList("/repos/" + org + "/" + repo + "/pulls/" + prNumber + "/reviews",
                new ParameterizedTypeReference<>() {});
    }

    @Override
    public List<RawReviewComment> listReviewComments(String org, String repo, int prNumber) {
        return fetchList("/repos/" + org + "/" + repo + "/pulls/" + prNumber + "/comments",
                new ParameterizedTypeReference<>() {});
    }

    @Override
    public List<RawIssue> listIssues(String org, String repo) {
        return fetchList("/repos/" + org + "/" + repo + "/issues?state=all",
                new ParameterizedTypeReference<>() {});
    }

    @Override
    public List<RawContributor> listContributors(String org, String repo) {
        return fetchList("/repos/" + org + "/" + repo + "/contributors",
                new ParameterizedTypeReference<>() {});
    }

    @Override
    public List<RawRelease> listReleases(String org, String repo) {
        return fetchList("/repos/" + org + "/" + repo + "/releases",
                new ParameterizedTypeReference<>() {});
    }

    @Override
    public List<RawTeam> listTeams(String org) {
        return fetchList("/orgs/" + org + "/teams", new ParameterizedTypeReference<>() {});
    }

    @Override
    public List<RawMember> listTeamMembers(String org, String teamSlug) {
        return fetchList("/orgs/" + org + "/teams/" + teamSlug + "/members",
                new ParameterizedTypeReference<>() {});
    }

    @Override
    public List<RawTeamRepo> listTeamRepos(String org, String teamSlug) {
        return fetchList("/orgs/" + org + "/teams/" + teamSlug + "/repos",
                new ParameterizedTypeReference<>() {});
    }

    @Override
    public List<RawCommitComment> listCommitComments(String org, String repo) {
        return fetchList("/repos/" + org + "/" + repo + "/comments",
                new ParameterizedTypeReference<>() {});
    }

    @Override
    public List<RawIssueComment> listIssueComments(String org, String repo) {
        return fetchList("/repos/" + org + "/" + repo + "/issues/comments",
                new ParameterizedTypeReference<>() {});
    }

    @Override
    public List<RawLabel> listLabels(String org, String repo) {
        return fetchList("/repos/" + org + "/" + repo + "/labels",
                new ParameterizedTypeReference<>() {});
    }

    @Override
    public List<RawMilestone> listMilestones(String org, String repo) {
        return fetchList("/repos/" + org + "/" + repo + "/milestones?state=all",
                new ParameterizedTypeReference<>() {});
    }

    @Override
    public List<RawWorkflow> listWorkflows(String org, String repo) {
        return fetchList("/repos/" + org + "/" + repo + "/actions/workflows",
                new ParameterizedTypeReference<>() {});
    }

    @Override
    public List<RawWorkflowRun> listWorkflowRuns(String org, String repo) {
        return fetchList("/repos/" + org + "/" + repo + "/actions/runs",
                new ParameterizedTypeReference<>() {});
    }

    @Override
    public List<RawDeployment> listDeployments(String org, String repo) {
        return fetchList("/repos/" + org + "/" + repo + "/deployments",
                new ParameterizedTypeReference<>() {});
    }

    @Override
    public List<RawCollaborator> listCollaborators(String org, String repo) {
        return fetchList("/repos/" + org + "/" + repo + "/collaborators",
                new ParameterizedTypeReference<>() {});
    }

    @Override
    public List<RawRuleset> listRulesets(String org, String repo) {
        return fetchList("/repos/" + org + "/" + repo + "/rulesets",
                new ParameterizedTypeReference<>() {});
    }
}
