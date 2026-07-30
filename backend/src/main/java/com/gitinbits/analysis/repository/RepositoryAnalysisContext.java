package com.gitinbits.analysis.repository;

import com.gitinbits.dto.response.repo.*;
import java.util.Collections;
import java.util.List;

/**
 * Represents the complete analysis context for ONE repository.
 * Contains all metadata required by repository analysis without any GitHub-specific coupling.
 *
 * @param repo         Repository metadata
 * @param commits      List of commits in the repository
 * @param pullRequests List of pull requests in the repository
 * @param issues       List of issues in the repository
 * @param reviews      List of pull request reviews in the repository
 * @param branches     List of branches in the repository
 * @param contributors List of contributors in the repository
 */
public record RepositoryAnalysisContext(
        RepoDto repo,
        List<CommitDto> commits,
        List<PullRequestDto> pullRequests,
        List<IssueDto> issues,
        List<ReviewDto> reviews,
        List<BranchDto> branches,
        List<ContributorDto> contributors
) {
    public RepositoryAnalysisContext {
        commits = commits != null ? List.copyOf(commits) : Collections.emptyList();
        pullRequests = pullRequests != null ? List.copyOf(pullRequests) : Collections.emptyList();
        issues = issues != null ? List.copyOf(issues) : Collections.emptyList();
        reviews = reviews != null ? List.copyOf(reviews) : Collections.emptyList();
        branches = branches != null ? List.copyOf(branches) : Collections.emptyList();
        contributors = contributors != null ? List.copyOf(contributors) : Collections.emptyList();
    }
}
