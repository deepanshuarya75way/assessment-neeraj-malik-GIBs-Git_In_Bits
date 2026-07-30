package com.gitinbits.ai.evidence;

import com.gitinbits.persistence.document.CommitDoc;
import com.gitinbits.persistence.document.IssueDoc;
import com.gitinbits.persistence.document.PullRequestDoc;
import com.gitinbits.persistence.repository.CommitRepository;
import com.gitinbits.persistence.repository.IssueRepository;
import com.gitinbits.persistence.repository.PullRequestRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RepositoryEvidenceService {

    private final CommitRepository commitRepository;
    private final IssueRepository issueRepository;
    private final PullRequestRepository prRepository;

    public RepositoryEvidenceService(
            CommitRepository commitRepository,
            IssueRepository issueRepository,
            PullRequestRepository prRepository) {
        this.commitRepository = commitRepository;
        this.issueRepository = issueRepository;
        this.prRepository = prRepository;
    }

    public RepositoryEvidence getEvidence(String owner, String repoName) {
        List<CommitDoc> commits = commitRepository.findByOwnerAndRepoName(owner, repoName);
        List<IssueDoc> issues = issueRepository.findByOwnerAndRepoName(owner, repoName);
        List<PullRequestDoc> prs = prRepository.findByOwnerAndRepoName(owner, repoName);

        int openIssuesCount = (int) issues.stream().filter(i -> "open".equalsIgnoreCase(i.state())).count();
        int openPrsCount = (int) prs.stream().filter(pr -> "open".equalsIgnoreCase(pr.state())).count();

        // Calculate contributors
        Map<String, Long> authorCounts = commits.stream()
                .filter(c -> c.authorName() != null)
                .collect(Collectors.groupingBy(CommitDoc::authorName, Collectors.counting()));

        int totalContributors = authorCounts.size();
        List<String> topContributors = authorCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .toList();

        // Calculate average commits per week
        double avgCommitsPerWeek = 0;
        if (!commits.isEmpty()) {
            Instant earliest = commits.stream()
                    .map(CommitDoc::synchronizedAt) // Fallback since authorDate is string ISO, we can just use synchronizedAt for a quick estimate
                    .min(Instant::compareTo)
                    .orElse(Instant.now());
            long days = ChronoUnit.DAYS.between(earliest, Instant.now());
            long weeks = days / 7;
            if (weeks < 1) weeks = 1;
            avgCommitsPerWeek = (double) commits.size() / weeks;
        }

        // Recent commit messages
        List<String> recentCommitMessages = commitRepository.findTop10ByOwnerAndRepoNameOrderByAuthorDateDesc(owner, repoName)
                .stream()
                .map(c -> c.authorName() + ": " + c.message())
                .toList();

        // Recent pull request titles
        List<String> recentPullRequests = prs.stream()
                .sorted((a, b) -> {
                    // Sort by number descending (assuming newer PRs have higher numbers)
                    if (a.number() != null && b.number() != null) {
                        return b.number().compareTo(a.number());
                    }
                    return 0;
                })
                .limit(10)
                .map(pr -> "#" + pr.number() + ": " + pr.title() + " [" + pr.state() + "]")
                .toList();

        return new RepositoryEvidence(
                totalContributors,
                topContributors,
                Math.round(avgCommitsPerWeek * 10.0) / 10.0,
                openIssuesCount,
                openPrsCount,
                recentCommitMessages,
                recentPullRequests
        );
    }

    public RepositoryTimeline getTimeline(String owner, String repoName) {
        CommitDoc firstCommit = commitRepository.findFirstByOwnerAndRepoNameOrderByAuthorDateAsc(owner, repoName);
        long totalCommits = commitRepository.countByOwnerAndRepoName(owner, repoName);

        String firstDate = firstCommit != null ? firstCommit.authorDate() : "Unknown";
        String firstMessage = firstCommit != null ? firstCommit.message() : "Unknown";
        String firstAuthor = firstCommit != null ? firstCommit.authorName() : "Unknown";

        return new RepositoryTimeline(
                firstDate,
                firstMessage,
                firstAuthor,
                totalCommits
        );
    }
}
