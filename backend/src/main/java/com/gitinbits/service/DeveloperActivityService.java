package com.gitinbits.service;

import com.gitinbits.dto.response.repo.DeveloperActivitySummaryDto;
import com.gitinbits.persistence.document.CommitDoc;
import com.gitinbits.persistence.document.PullRequestDoc;
import com.gitinbits.persistence.repository.CommitRepository;
import com.gitinbits.persistence.repository.PullRequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeveloperActivityService {

    private final CommitRepository commitRepository;
    private final PullRequestRepository pullRequestRepository;

    public DeveloperActivityService(
            CommitRepository commitRepository,
            PullRequestRepository pullRequestRepository
    ) {
        this.commitRepository = commitRepository;
        this.pullRequestRepository = pullRequestRepository;
    }

    public List<DeveloperActivitySummaryDto> getTopDevelopers(String owner) {
        return commitRepository.getDeveloperActivitySummary(owner);
    }

    public List<CommitDoc> getDeveloperRecentActivity(String owner, String authorName) {
        // Fetch up to 10 most recent commits for this author across the org
        List<CommitDoc> allCommits = commitRepository.findByOwnerAndAuthorNameOrderByAuthorDateDesc(owner, authorName);
        return allCommits.size() > 10 ? allCommits.subList(0, 10) : allCommits;
    }

    public List<PullRequestDoc> getDeveloperRecentPRs(String owner, String authorName) {
        List<PullRequestDoc> allPrs = pullRequestRepository.findByOwnerAndUserLoginOrderByUpdatedAtDesc(owner, authorName);
        return allPrs.size() > 5 ? allPrs.subList(0, 5) : allPrs;
    }
}
