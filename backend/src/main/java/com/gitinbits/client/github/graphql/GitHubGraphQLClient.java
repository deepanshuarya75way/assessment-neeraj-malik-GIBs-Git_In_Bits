package com.gitinbits.client.github.graphql;

import com.gitinbits.dto.response.repo.CommitDto;
import java.time.Instant;
import java.util.List;

public interface GitHubGraphQLClient {
    List<CommitDto> fetchCommitsWithStats(String org, String repo, Instant since);
    List<PullRequestWithReviews> fetchPullsWithReviews(String org, String repo);
}
