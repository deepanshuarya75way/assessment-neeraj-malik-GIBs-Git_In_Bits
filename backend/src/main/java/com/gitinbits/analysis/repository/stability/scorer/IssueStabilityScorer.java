package com.gitinbits.analysis.repository.stability.scorer;

import com.gitinbits.dto.response.repo.IssueDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IssueStabilityScorer {
    public int score(List<IssueDto> issues) {
        // V1 placeholder metric
        return 100;
    }
}
