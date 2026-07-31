package com.gitinbits.service;

import com.gitinbits.persistence.document.PullRequestDoc;
import com.gitinbits.persistence.document.IssueDoc;
import com.gitinbits.persistence.document.WorkflowRunDoc;
import com.gitinbits.persistence.repository.PullRequestRepository;
import com.gitinbits.persistence.repository.IssueRepository;
import com.gitinbits.persistence.repository.WorkflowRunRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrganizationEvidenceService {

    private final PullRequestRepository prRepository;
    private final IssueRepository issueRepository;
    private final WorkflowRunRepository workflowRunRepository;
    private final com.gitinbits.persistence.repository.CommitRepository commitRepository;

    public OrganizationEvidenceService(PullRequestRepository prRepository, IssueRepository issueRepository, WorkflowRunRepository workflowRunRepository, com.gitinbits.persistence.repository.CommitRepository commitRepository) {
        this.prRepository = prRepository;
        this.issueRepository = issueRepository;
        this.workflowRunRepository = workflowRunRepository;
        this.commitRepository = commitRepository;
    }

    public record OrganizationEvidence(
            List<String> activeWorkstreams,
            List<String> recentlyCompleted,
            List<String> needsAttention,
            long totalPrsMerged,
            long totalIssuesClosed,
            long totalWorkflowFailures
    ) {}

    public OrganizationEvidence gatherEvidence(String owner, Instant since, Instant until) {
        String sinceStr = DateTimeFormatter.ISO_INSTANT.format(since);
        String untilStr = DateTimeFormatter.ISO_INSTANT.format(until);

        List<PullRequestDoc> recentPrs = prRepository.findByOwnerAndUpdatedAtBetweenOrderByUpdatedAtDesc(owner, sinceStr, untilStr);
        List<IssueDoc> recentIssues = issueRepository.findByOwnerAndUpdatedAtBetweenOrderByUpdatedAtDesc(owner, sinceStr, untilStr);
        List<WorkflowRunDoc> recentWorkflows = workflowRunRepository.findByOwnerAndUpdatedAtBetweenOrderByUpdatedAtDesc(owner, sinceStr, untilStr);
        List<com.gitinbits.persistence.document.CommitDoc> recentCommits = commitRepository.findByOwnerAndAuthorDateBetweenOrderByAuthorDateDesc(owner, sinceStr, untilStr);

        // Active Workstreams: Titles of recently updated open PRs AND recent commit messages
        List<String> activeWorkstreams = recentPrs.stream()
                .filter(pr -> "open".equalsIgnoreCase(pr.state()))
                .map(pr -> "[PR] " + pr.repoName() + ": " + pr.title())
                .collect(Collectors.toList());
                
        // Add distinct commit messages to workstreams if not already covered
        List<String> commitWorkstreams = recentCommits.stream()
                .map(c -> "[Commit] " + c.repoName() + ": " + c.message().split("\n")[0]) // just first line of commit
                .distinct()
                .collect(Collectors.toList());
                
        activeWorkstreams.addAll(commitWorkstreams);
        activeWorkstreams = activeWorkstreams.stream().distinct().limit(7).collect(Collectors.toList());

        // Recently Completed: Titles of merged PRs
        List<String> recentlyCompleted = recentPrs.stream()
                .filter(PullRequestDoc::merged)
                .map(pr -> pr.repoName() + ": " + pr.title())
                .limit(5)
                .collect(Collectors.toList());

        // Needs Attention: CI failures or old open PRs
        List<String> needsAttention = recentWorkflows.stream()
                .filter(w -> "failure".equalsIgnoreCase(w.conclusion()))
                .map(w -> w.repoName() + ": CI Failed for workflow '" + w.name() + "'")
                .limit(3)
                .collect(Collectors.toList());

        long oldPrs = recentPrs.stream()
                .filter(pr -> "open".equalsIgnoreCase(pr.state()))
                .filter(pr -> Instant.parse(pr.createdAt()).isBefore(Instant.now().minusSeconds(86400 * 5))) // Older than 5 days
                .count();
        if (oldPrs > 0) {
            needsAttention.add(oldPrs + " PRs have been open for more than 5 days.");
        }

        long totalMerged = recentPrs.stream().filter(PullRequestDoc::merged).count();
        long totalIssues = recentIssues.stream().filter(i -> "closed".equalsIgnoreCase(i.state())).count();
        long totalFailures = recentWorkflows.stream().filter(w -> "failure".equalsIgnoreCase(w.conclusion())).count();

        return new OrganizationEvidence(
                activeWorkstreams,
                recentlyCompleted,
                needsAttention,
                totalMerged,
                totalIssues,
                totalFailures
        );
    }
}
