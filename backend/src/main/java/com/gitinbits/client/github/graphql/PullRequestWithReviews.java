package com.gitinbits.client.github.graphql;

import com.gitinbits.dto.response.repo.PullRequestDto;
import com.gitinbits.dto.response.repo.ReviewDto;

import java.util.List;

public record PullRequestWithReviews(
        PullRequestDto pullRequest,
        List<ReviewDto> reviews
) {}
