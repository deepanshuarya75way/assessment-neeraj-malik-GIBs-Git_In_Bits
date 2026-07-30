package com.gitinbits.ai.evidence;

import java.util.List;

public record RepositoryEvidence(
    int totalContributors,
    List<String> topContributors,
    double averageCommitsPerWeek,
    int openIssuesCount,
    int openPullRequestsCount,
    List<String> recentCommitMessages,
    List<String> recentPullRequests
) {}
