package com.gitinbits.analysis.repository.stability.scorer;

import com.gitinbits.dto.response.repo.PullRequestDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PullRequestStabilityScorer {
    public int score(List<PullRequestDto> pullRequests) {
        // V1 placeholder metric
        return 100;
    }
}
