package com.gitinbits.analysis.repository.builder;

import com.gitinbits.analysis.common.AnalysisContextBuilder;
import com.gitinbits.analysis.repository.RepositoryAnalysisContext;
import com.gitinbits.dto.context.GitHubContext;
import com.gitinbits.dto.response.repo.*;
import com.gitinbits.persistence.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Dedicated builder responsible for gathering existing GitHub metadata
 * and assembling a fully populated {@link RepositoryAnalysisContext}.
 *
 * <p>This class is purely an assembly component. It contains no health formulas,
 * no scoring logic, no REST controller coupling, and no direct HTTP/GitHub client calls.
 *
 * <p>Queries are performed using compound-indexed fields {@code owner} and {@code repoName},
 * enabling O(log n) lookups regardless of total document count across all repositories.
 */
@Service
public class RepositoryContextBuilder implements AnalysisContextBuilder<RepositoryAnalysisContext> {

    private static final Logger log = LoggerFactory.getLogger(RepositoryContextBuilder.class);

    private final RepoRepository repoRepository;
    private final BranchRepository branchRepository;
    private final CommitRepository commitRepository;
    private final PullRequestRepository pullRequestRepository;
    private final IssueRepository issueRepository;
    private final ContributorRepository contributorRepository;
    private final ReviewRepository reviewRepository;
    private final DocumentToDtoMapper mapper;

    public RepositoryContextBuilder(
            RepoRepository repoRepository,
            BranchRepository branchRepository,
            CommitRepository commitRepository,
            PullRequestRepository pullRequestRepository,
            IssueRepository issueRepository,
            ContributorRepository contributorRepository,
            ReviewRepository reviewRepository,
            DocumentToDtoMapper mapper) {
        this.repoRepository = repoRepository;
        this.branchRepository = branchRepository;
        this.commitRepository = commitRepository;
        this.pullRequestRepository = pullRequestRepository;
        this.issueRepository = issueRepository;
        this.contributorRepository = contributorRepository;
        this.reviewRepository = reviewRepository;
        this.mapper = mapper;
    }

    @Override
    public RepositoryAnalysisContext build(GitHubContext context, String repositoryName) {
        String owner        = context.accountName();
        String repoName     = repositoryName;
        String repoFullName = owner + "/" + repoName;

        log.debug("Building RepositoryAnalysisContext for {}/{}", owner, repoName);
        log.debug("Loading repository data from MongoDB (owner={}, repoName={})", owner, repoName);

        RepoDto repo                    = fetchRepo(repoFullName);
        List<BranchDto> branches        = fetchBranches(owner, repoName);
        List<CommitDto> commits         = fetchCommits(owner, repoName);
        List<PullRequestDto> pullRequests = fetchPullRequests(owner, repoName);
        List<IssueDto> issues           = fetchIssues(owner, repoName);
        List<ContributorDto> contributors = fetchContributors(owner, repoName);
        List<ReviewDto> reviews         = fetchReviews(owner, repoName);

        log.debug("Fetched metadata from MongoDB for {}: commits={}, branches={}, prs={}, reviews={}, issues={}, contributors={}",
                repoFullName,
                commits.size(), branches.size(), pullRequests.size(), reviews.size(), issues.size(), contributors.size());

        RepositoryAnalysisContext analysisContext = new RepositoryAnalysisContext(
                repo, commits, pullRequests, issues, reviews, branches, contributors
        );

        log.debug("RepositoryAnalysisContext built successfully for {}", repoFullName);
        return analysisContext;
    }

    // ─── Private Helper Methods ────────────────────────────────────────────────

    private RepoDto fetchRepo(String repoFullName) {
        return repoRepository.findById(repoFullName)
                .map(mapper::toRepoDto)
                .orElse(null);
    }

    private List<BranchDto> fetchBranches(String owner, String repoName) {
        return branchRepository.findByOwnerAndRepoName(owner, repoName)
                .stream().map(mapper::toBranchDto).collect(Collectors.toList());
    }

    private List<CommitDto> fetchCommits(String owner, String repoName) {
        return commitRepository.findByOwnerAndRepoName(owner, repoName)
                .stream().map(mapper::toCommitDto).collect(Collectors.toList());
    }

    private List<PullRequestDto> fetchPullRequests(String owner, String repoName) {
        return pullRequestRepository.findByOwnerAndRepoName(owner, repoName)
                .stream().map(mapper::toPullRequestDto).collect(Collectors.toList());
    }

    private List<IssueDto> fetchIssues(String owner, String repoName) {
        return issueRepository.findByOwnerAndRepoName(owner, repoName)
                .stream().map(mapper::toIssueDto).collect(Collectors.toList());
    }

    private List<ContributorDto> fetchContributors(String owner, String repoName) {
        return contributorRepository.findByOwnerAndRepoName(owner, repoName)
                .stream().map(mapper::toContributorDto).collect(Collectors.toList());
    }

    private List<ReviewDto> fetchReviews(String owner, String repoName) {
        return reviewRepository.findByOwnerAndRepoName(owner, repoName)
                .stream().map(mapper::toReviewDto).collect(Collectors.toList());
    }
}
