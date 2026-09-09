package com.gitinbits.client.github.graphql;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitinbits.config.GitHubProperties;
import com.gitinbits.dto.response.repo.CommitDto;
import com.gitinbits.dto.response.repo.PullRequestDto;
import com.gitinbits.dto.response.repo.ReviewDto;
import com.gitinbits.exception.GitHubAuthException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class GitHubGraphQLClientImpl implements GitHubGraphQLClient {

    private static final Logger log = LoggerFactory.getLogger(GitHubGraphQLClientImpl.class);
    private static final String GRAPHQL_URL = "https://api.github.com/graphql";

    private final RestClient restClient;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final ObjectMapper mapper;

    public GitHubGraphQLClientImpl(RestClient restClient,
                                   OAuth2AuthorizedClientService authorizedClientService,
                                   ObjectMapper mapper) {
        this.restClient = restClient;
        this.authorizedClientService = authorizedClientService;
        this.mapper = mapper;
    }

    private String getAccessToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof OAuth2AuthenticationToken oauthToken)) {
            throw new GitHubAuthException("No active GitHub OAuth2 session.");
        }
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(),
                oauthToken.getName()
        );
        if (client == null || client.getAccessToken() == null) {
            throw new GitHubAuthException("GitHub access token not found.");
        }
        return client.getAccessToken().getTokenValue();
    }

    private JsonNode executeQuery(String query, Map<String, Object> variables) {
        try {
            String token = getAccessToken();
            Map<String, Object> body = Map.of("query", query, "variables", variables);
            
            JsonNode response = restClient.post()
                    .uri(GRAPHQL_URL)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);

            if (response != null && response.has("errors")) {
                log.warn("GraphQL errors: {}", response.get("errors"));
            }
            return response != null ? response.get("data") : null;
        } catch (Exception e) {
            log.error("GraphQL query failed", e);
            return null;
        }
    }

    @Override
    public List<CommitDto> fetchCommitsWithStats(String org, String repo, Instant since) {
        String query = """
            query($owner: String!, $repo: String!, $since: GitTimestamp!) {
              repository(owner: $owner, name: $repo) {
                defaultBranchRef {
                  target {
                    ... on Commit {
                      history(since: $since, first: 100) {
                        nodes {
                          oid
                          message
                          authoredDate
                          additions
                          deletions
                          changedFilesIfAvailable
                          author {
                            name
                            email
                            user { login }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
        """;

        JsonNode data = executeQuery(query, Map.of("owner", org, "repo", repo, "since", since.toString()));
        List<CommitDto> commits = new ArrayList<>();
        
        if (data == null || !data.hasNonNull("repository")) return commits;
        JsonNode repository = data.get("repository");
        if (!repository.hasNonNull("defaultBranchRef")) return commits;
        JsonNode target = repository.get("defaultBranchRef").get("target");
        if (!target.hasNonNull("history")) return commits;
        JsonNode nodes = target.get("history").get("nodes");
        
        if (nodes != null && nodes.isArray()) {
            for (JsonNode node : nodes) {
                String sha = node.path("oid").asText(null);
                String message = node.path("message").asText(null);
                String authoredDate = node.path("authoredDate").asText(null);
                Integer additions = node.path("additions").asInt(0);
                Integer deletions = node.path("deletions").asInt(0);
                Integer filesChanged = node.path("changedFilesIfAvailable").asInt(0);
                
                String authorName = null, authorEmail = null, login = null;
                JsonNode authorNode = node.get("author");
                if (authorNode != null) {
                    authorName = authorNode.path("name").asText(null);
                    authorEmail = authorNode.path("email").asText(null);
                    JsonNode userNode = authorNode.get("user");
                    if (userNode != null && !userNode.isNull()) {
                        login = userNode.path("login").asText(null);
                    }
                }
                
                commits.add(new CommitDto(
                        sha, authorName, authorEmail, authorName, authorEmail, login,
                        message, authoredDate, null, List.of(), null, null,
                        filesChanged, additions, deletions, additions + deletions, List.of()
                ));
            }
        }
        return commits;
    }

    @Override
    public List<PullRequestWithReviews> fetchPullsWithReviews(String org, String repo) {
        String query = """
            query($owner: String!, $repo: String!) {
              repository(owner: $owner, name: $repo) {
                pullRequests(first: 100, orderBy: {field: UPDATED_AT, direction: DESC}) {
                  nodes {
                    number
                    title
                    bodyText
                    state
                    createdAt
                    updatedAt
                    mergedAt
                    closedAt
                    additions
                    deletions
                    changedFiles
                    author { login }
                    reviews(first: 50) {
                      nodes {
                        author { login }
                        state
                        bodyText
                        createdAt
                        commit { oid }
                      }
                    }
                  }
                }
              }
            }
        """;

        JsonNode data = executeQuery(query, Map.of("owner", org, "repo", repo));
        List<PullRequestWithReviews> results = new ArrayList<>();
        
        if (data == null || !data.hasNonNull("repository")) return results;
        JsonNode pullRequests = data.get("repository").get("pullRequests");
        if (!pullRequests.hasNonNull("nodes")) return results;
        
        JsonNode prNodes = pullRequests.get("nodes");
        if (prNodes != null && prNodes.isArray()) {
            for (JsonNode prNode : prNodes) {
                Integer number = prNode.path("number").asInt(0);
                String title = prNode.path("title").asText(null);
                String bodyText = prNode.path("bodyText").asText(null);
                String state = prNode.path("state").asText(null);
                String createdAt = prNode.path("createdAt").asText(null);
                String updatedAt = prNode.path("updatedAt").asText(null);
                String mergedAt = prNode.hasNonNull("mergedAt") ? prNode.path("mergedAt").asText(null) : null;
                String closedAt = prNode.hasNonNull("closedAt") ? prNode.path("closedAt").asText(null) : null;
                Integer additions = prNode.path("additions").asInt(0);
                Integer deletions = prNode.path("deletions").asInt(0);
                Integer changedFiles = prNode.path("changedFiles").asInt(0);
                
                String author = null;
                JsonNode authorNode = prNode.get("author");
                if (authorNode != null && !authorNode.isNull()) {
                    author = authorNode.path("login").asText(null);
                }

                PullRequestDto prDto = new PullRequestDto(
                        number, title, bodyText, author, state, false, false,
                        createdAt, updatedAt, closedAt, mergedAt, null, null,
                        List.of(), List.of(), List.of(), null, 0, changedFiles,
                        additions, deletions, 0, 0, null, null
                );
                
                List<ReviewDto> reviewsList = new ArrayList<>();
                JsonNode reviewsNode = prNode.get("reviews");
                if (reviewsNode != null && reviewsNode.hasNonNull("nodes")) {
                    for (JsonNode rev : reviewsNode.get("nodes")) {
                        String revState = rev.path("state").asText(null);
                        String revBody = rev.path("bodyText").asText(null);
                        String revCreatedAt = rev.path("createdAt").asText(null);
                        String revAuthor = null;
                        if (rev.hasNonNull("author")) revAuthor = rev.get("author").path("login").asText(null);
                        String commitId = null;
                        if (rev.hasNonNull("commit")) commitId = rev.get("commit").path("oid").asText(null);
                        
                        reviewsList.add(new ReviewDto(revAuthor, revState, revCreatedAt, revBody, commitId));
                    }
                }
                
                results.add(new PullRequestWithReviews(prDto, reviewsList));
            }
        }
        return results;
    }
}
