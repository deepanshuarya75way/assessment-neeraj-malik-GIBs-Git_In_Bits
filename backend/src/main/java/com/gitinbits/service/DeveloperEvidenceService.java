package com.gitinbits.service;

import com.gitinbits.persistence.document.CommitDoc;
import com.gitinbits.persistence.document.PullRequestDoc;
import com.gitinbits.persistence.repository.CommitRepository;
import com.gitinbits.persistence.repository.IssueRepository;
import com.gitinbits.persistence.repository.PullRequestRepository;
import com.gitinbits.persistence.repository.ReviewRepository;
import com.gitinbits.persistence.repository.WorkflowRunRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeveloperEvidenceService {

    private final CommitRepository commitRepository;
    private final PullRequestRepository pullRequestRepository;
    private final ReviewRepository reviewRepository;
    private final IssueRepository issueRepository;
    private final WorkflowRunRepository workflowRunRepository;

    public DeveloperEvidenceService(
            CommitRepository commitRepository,
            PullRequestRepository pullRequestRepository,
            ReviewRepository reviewRepository,
            IssueRepository issueRepository,
            WorkflowRunRepository workflowRunRepository
    ) {
        this.commitRepository = commitRepository;
        this.pullRequestRepository = pullRequestRepository;
        this.reviewRepository = reviewRepository;
        this.issueRepository = issueRepository;
        this.workflowRunRepository = workflowRunRepository;
    }

    public record DeveloperEvidence(
            String authorName,
            long commitCount,
            long totalAdditions,
            long totalDeletions,
            long prsOpened,
            long prsMerged,
            String avgMergeTime,
            long reviewsConducted,
            long issuesResolved,
            long workflowFailures,
            long workflowSuccesses
    ) {}
    
    public List<com.gitinbits.dto.response.repo.DeveloperActivitySummaryDto> getTopDevelopers(String owner) {
        return commitRepository.getDeveloperActivitySummary(owner);
    }

    public DeveloperEvidence gatherEvidence(String owner, String authorName) {
        // Default to last 30 days if no date provided
        return gatherEvidence(owner, authorName, Instant.now().minus(Duration.ofDays(30)), Instant.now());
    }

    public DeveloperEvidence gatherEvidence(String owner, String authorName, Instant since, Instant until) {
        String sinceStr = java.time.format.DateTimeFormatter.ISO_INSTANT.format(since);
        String untilStr = java.time.format.DateTimeFormatter.ISO_INSTANT.format(until);

        // 1. Commits Evidence
        List<CommitDoc> commits = commitRepository.findByOwnerAndAuthorNameAndAuthorDateBetweenOrderByAuthorDateDesc(owner, authorName, sinceStr, untilStr);
        long totalAdditions = commits.stream().mapToLong(c -> c.additions() != null ? c.additions() : 0).sum();
        long totalDeletions = commits.stream().mapToLong(c -> c.deletions() != null ? c.deletions() : 0).sum();

        // 2. PR Evidence
        List<PullRequestDoc> prs = pullRequestRepository.findByOwnerAndUserLoginAndUpdatedAtBetweenOrderByUpdatedAtDesc(owner, authorName, sinceStr, untilStr);
        long prsOpened = prs.size();
        List<PullRequestDoc> mergedPrs = prs.stream().filter(PullRequestDoc::merged).toList();
        long prsMerged = mergedPrs.size();
        
        long totalSeconds = 0;
        for (PullRequestDoc pr : mergedPrs) {
            if (pr.createdAt() != null && pr.mergedAt() != null) {
                try {
                    Instant created = Instant.parse(pr.createdAt());
                    Instant merged = Instant.parse(pr.mergedAt());
                    totalSeconds += Duration.between(created, merged).getSeconds();
                } catch (Exception e) {
                    // Ignore parsing errors for simplicity
                }
            }
        }
        String avgMergeTime = prsMerged > 0 ? (totalSeconds / prsMerged / 3600) + " hours" : "N/A";

        // 3. Review Evidence
        long reviewsConducted = reviewRepository.findByOwnerAndReviewerAndSubmittedAtBetweenOrderBySubmittedAtDesc(owner, authorName, sinceStr, untilStr).size();

        // 4. Issue Evidence
        long issuesAssigned = issueRepository.findByOwnerAndUserLoginAndUpdatedAtBetweenOrderByUpdatedAtDesc(owner, authorName, sinceStr, untilStr).size();

        // 5. Workflow Evidence
        List<com.gitinbits.persistence.document.WorkflowRunDoc> runs = workflowRunRepository.findByOwnerAndActorLoginAndUpdatedAtBetweenOrderByUpdatedAtDesc(owner, authorName, sinceStr, untilStr);
        long workflowFailures = runs.stream().filter(r -> "failure".equalsIgnoreCase(r.conclusion())).count();
        long workflowSuccesses = runs.stream().filter(r -> "success".equalsIgnoreCase(r.conclusion())).count();

        return new DeveloperEvidence(
                authorName,
                commits.size(),
                totalAdditions,
                totalDeletions,
                prsOpened,
                prsMerged,
                avgMergeTime,
                reviewsConducted,
                issuesAssigned,
                workflowFailures,
                workflowSuccesses
        );
    }
}
