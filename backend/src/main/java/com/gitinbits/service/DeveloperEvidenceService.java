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

    public record OpenPrDetails(String title, String openTime) {}

    public record DeveloperEvidence(
            String authorName,
            long commitCount,
            long totalAdditions,
            long totalDeletions,
            long prsOpened,
            long prsMerged,
            String avgMergeTime,
            List<OpenPrDetails> activePrs,
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

    public DeveloperEvidence gatherEvidence(String owner, String identifier, Instant since, Instant until) {
        String sinceStr = java.time.format.DateTimeFormatter.ISO_INSTANT.format(since);
        String untilStr = java.time.format.DateTimeFormatter.ISO_INSTANT.format(until);

        // 1. Commits Evidence (Searching by either authorName or githubLogin)
        List<CommitDoc> commits = commitRepository.findDeveloperCommits(owner, identifier, sinceStr, untilStr);
        
        // Resolve exactly what the login and name are for subsequent queries
        String githubLogin = identifier;
        String resolvedAuthorName = identifier;
        if (!commits.isEmpty()) {
            CommitDoc first = commits.get(0);
            if (first.githubLogin() != null) githubLogin = first.githubLogin();
            if (first.authorName() != null) resolvedAuthorName = first.authorName();
        }

        long totalAdditions = commits.stream().mapToLong(c -> c.additions() != null ? c.additions() : 0).sum();
        long totalDeletions = commits.stream().mapToLong(c -> c.deletions() != null ? c.deletions() : 0).sum();

        // 2. PR Evidence
        List<PullRequestDoc> prs = pullRequestRepository.findByOwnerAndUserLoginAndUpdatedAtBetweenOrderByUpdatedAtDesc(owner, githubLogin, sinceStr, untilStr);
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
        String avgMergeTime = "N/A";
        if (prsMerged > 0) {
            long avgSeconds = totalSeconds / prsMerged;
            if (avgSeconds < 60) {
                avgMergeTime = avgSeconds + " secs";
            } else if (avgSeconds < 3600) {
                avgMergeTime = (avgSeconds / 60) + " mins";
            } else if (avgSeconds < 86400) {
                avgMergeTime = (avgSeconds / 3600) + " hrs " + ((avgSeconds % 3600) / 60) + " mins";
            } else {
                avgMergeTime = (avgSeconds / 86400) + " days " + ((avgSeconds % 86400) / 3600) + " hrs";
            }
        }

        // Calculate open time for currently open PRs
        List<PullRequestDoc> openPrsList = prs.stream().filter(pr -> "open".equalsIgnoreCase(pr.state())).toList();
        List<OpenPrDetails> activePrs = new java.util.ArrayList<>();
        for (PullRequestDoc pr : openPrsList) {
            if (pr.createdAt() != null) {
                try {
                    Instant created = Instant.parse(pr.createdAt());
                    long seconds = Duration.between(created, Instant.now()).getSeconds();
                    String timeStr = "";
                    if (seconds < 60) timeStr = seconds + " secs";
                    else if (seconds < 3600) timeStr = (seconds / 60) + " mins";
                    else if (seconds < 86400) timeStr = (seconds / 3600) + " hrs " + ((seconds % 3600) / 60) + " m";
                    else timeStr = (seconds / 86400) + " days " + ((seconds % 86400) / 3600) + " h";
                    
                    activePrs.add(new OpenPrDetails(pr.title(), timeStr));
                } catch (Exception e) {}
            }
        }

        // 3. Review Evidence
        long reviewsConducted = reviewRepository.findByOwnerAndReviewerAndSubmittedAtBetweenOrderBySubmittedAtDesc(owner, githubLogin, sinceStr, untilStr).size();

        // 4. Issue Evidence
        long issuesAssigned = issueRepository.findByOwnerAndUserLoginAndUpdatedAtBetweenOrderByUpdatedAtDesc(owner, githubLogin, sinceStr, untilStr).size();

        // 5. Workflow Evidence
        List<com.gitinbits.persistence.document.WorkflowRunDoc> runs = workflowRunRepository.findByOwnerAndActorLoginAndUpdatedAtBetweenOrderByUpdatedAtDesc(owner, githubLogin, sinceStr, untilStr);
        long workflowFailures = runs.stream().filter(r -> "failure".equalsIgnoreCase(r.conclusion())).count();
        long workflowSuccesses = runs.stream().filter(r -> "success".equalsIgnoreCase(r.conclusion())).count();

        return new DeveloperEvidence(
                resolvedAuthorName,
                commits.size(),
                totalAdditions,
                totalDeletions,
                prsOpened,
                prsMerged,
                avgMergeTime,
                activePrs,
                reviewsConducted,
                issuesAssigned,
                workflowFailures,
                workflowSuccesses
        );
    }
}
