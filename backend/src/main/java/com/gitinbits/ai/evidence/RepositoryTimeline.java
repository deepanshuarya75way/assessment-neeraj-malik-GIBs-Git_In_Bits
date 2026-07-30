package com.gitinbits.ai.evidence;

public record RepositoryTimeline(
    String firstCommitDate,
    String firstCommitMessage,
    String firstCommitAuthor,
    long totalLifetimeCommits
) {}
