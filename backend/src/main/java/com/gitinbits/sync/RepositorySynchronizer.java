package com.gitinbits.sync;

import com.gitinbits.dto.context.GitHubContext;
import com.gitinbits.dto.response.repo.*;
import com.gitinbits.persistence.document.*;
import com.gitinbits.persistence.repository.*;
import com.gitinbits.service.github.GitHubRepoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RepositorySynchronizer {

    private static final Logger log = LoggerFactory.getLogger(RepositorySynchronizer.class);

    private final GitHubRepoService githubRepoService;
    private final GithubToDocumentMapper mapper;
    
    private final RepoRepository repoRepository;
    private final CommitRepository commitRepository;
    private final PullRequestRepository pullRequestRepository;
    private final IssueRepository issueRepository;
    private final ReviewRepository reviewRepository;
    private final BranchRepository branchRepository;
    private final ContributorRepository contributorRepository;
    private final ReleaseRepository releaseRepository;
    private final SyncMetadataRepository syncMetadataRepository;
    private final WorkflowRunRepository workflowRunRepository;

    public RepositorySynchronizer(
            GitHubRepoService githubRepoService,
            GithubToDocumentMapper mapper,
            RepoRepository repoRepository,
            CommitRepository commitRepository,
            PullRequestRepository pullRequestRepository,
            IssueRepository issueRepository,
            ReviewRepository reviewRepository,
            BranchRepository branchRepository,
            ContributorRepository contributorRepository,
            ReleaseRepository releaseRepository,
            SyncMetadataRepository syncMetadataRepository,
            WorkflowRunRepository workflowRunRepository
    ) {
        this.githubRepoService = githubRepoService;
        this.mapper = mapper;
        this.repoRepository = repoRepository;
        this.commitRepository = commitRepository;
        this.pullRequestRepository = pullRequestRepository;
        this.issueRepository = issueRepository;
        this.reviewRepository = reviewRepository;
        this.branchRepository = branchRepository;
        this.contributorRepository = contributorRepository;
        this.releaseRepository = releaseRepository;
        this.syncMetadataRepository = syncMetadataRepository;
        this.workflowRunRepository = workflowRunRepository;
    }

    public void syncIfNeeded(GitHubContext context, String repoFullName) {
        SyncMetadataDoc metadata = syncMetadataRepository.findById(repoFullName).orElse(null);
        if (metadata == null || metadata.lastSyncTimestamp() == null || 
            metadata.lastSyncTimestamp().isBefore(Instant.now().minus(Duration.ofMinutes(5)))) {
            log.info("Repository {} requires synchronization...", repoFullName);
            synchronize(context, repoFullName);
        }
    }

    public void synchronize(GitHubContext context, String repoFullName) {
        log.debug("synchronization started for {}", repoFullName);
        Instant start = Instant.now();
        Instant syncTime = start;

        // Split repoFullName (e.g. "owner/repo") into its parts
        // GitHubRepoService prepends context.accountName() internally,
        // so we must pass only the repo name, not the full "owner/repo" string.
        String[] parts = repoFullName.split("/", 2);
        String repoName = parts.length == 2 ? parts[1] : repoFullName;

        // Fetch Raw DTOs — pass repoName only, not repoFullName
        RepoDto repoDto = githubRepoService.getRepo(context, repoName);
        List<CommitDto> basicCommits = githubRepoService.listCommits(context, repoName);
        List<CommitDto> commits = new java.util.ArrayList<>();
        int count = 0;
        for (CommitDto c : basicCommits) {
            if (count < 30) {
                try {
                    commits.add(githubRepoService.getCommit(context, repoName, c.sha()));
                } catch (Exception e) {
                    log.warn("Failed to fetch detailed commit stats for sha: {}", c.sha(), e);
                    commits.add(c);
                }
            } else {
                commits.add(c);
            }
            count++;
        }
        List<PullRequestDto> pullRequests = githubRepoService.listPulls(context, repoName);
        List<IssueDto> issues = githubRepoService.listIssues(context, repoName);
        List<BranchDto> branches = githubRepoService.listBranches(context, repoName);
        List<ContributorDto> contributors = githubRepoService.listContributors(context, repoName);
        List<ReleaseDto> releases = githubRepoService.listReleases(context, repoName);
        List<WorkflowRunDto> workflows = githubRepoService.listWorkflowRuns(context, repoName);

        // Map and Save Repo
        RepoDoc repoDoc = mapper.toRepoDoc(repoDto, repoFullName, syncTime);
        repoRepository.save(repoDoc);

        // Map and Save Commits
        List<CommitDoc> commitDocs = commits.stream()
                .map(dto -> mapper.toCommitDoc(dto, repoFullName, syncTime))
                .collect(Collectors.toList());
        commitRepository.saveAll(commitDocs);

        // Map and Save PRs and fetch Reviews
        int reviewCount = 0;
        List<PullRequestDoc> prDocs = pullRequests.stream()
                .map(dto -> mapper.toPullRequestDoc(dto, repoFullName, syncTime))
                .collect(Collectors.toList());
        pullRequestRepository.saveAll(prDocs);

        int prLimitCount = 0;
        for (PullRequestDto pr : pullRequests) {
            if (prLimitCount >= 200) {
                break;
            }
            List<ReviewDto> reviews = githubRepoService.listReviews(context, repoName, pr.number());
            List<ReviewDoc> reviewDocs = reviews.stream()
                    .map(dto -> mapper.toReviewDoc(dto, repoFullName, pr.number(), syncTime))
                    .collect(Collectors.toList());
            reviewRepository.saveAll(reviewDocs);
            reviewCount += reviewDocs.size();
            prLimitCount++;
        }

        // Map and Save Issues
        List<IssueDoc> issueDocs = issues.stream()
                .map(dto -> mapper.toIssueDoc(dto, repoFullName, syncTime))
                .collect(Collectors.toList());
        issueRepository.saveAll(issueDocs);

        // Map and Save Branches
        List<BranchDoc> branchDocs = branches.stream()
                .map(dto -> mapper.toBranchDoc(dto, repoFullName, syncTime))
                .collect(Collectors.toList());
        branchRepository.saveAll(branchDocs);

        // Map and Save Contributors
        List<ContributorDoc> contributorDocs = contributors.stream()
                .map(dto -> mapper.toContributorDoc(dto, repoFullName, syncTime))
                .collect(Collectors.toList());
        contributorRepository.saveAll(contributorDocs);

        // Map and Save Releases
        List<ReleaseDoc> releaseDocs = releases.stream()
                .map(dto -> mapper.toReleaseDoc(dto, repoFullName, syncTime))
                .collect(Collectors.toList());
        releaseRepository.saveAll(releaseDocs);

        // Map and Save Workflow Runs
        List<WorkflowRunDoc> workflowDocs = workflows.stream()
                .map(dto -> mapper.toWorkflowRunDoc(dto, repoFullName, syncTime))
                .collect(Collectors.toList());
        workflowRunRepository.saveAll(workflowDocs);

        // Record Metadata
        String latestCommitSha = commits.isEmpty() ? null : commits.get(0).sha();
        SyncMetadataDoc metadata = syncMetadataRepository.findById(repoFullName).orElse(
                new SyncMetadataDoc(repoFullName, repoFullName.split("/")[0], null, null, null, null)
        );

        SyncMetadataDoc updatedMetadata = new SyncMetadataDoc(
                repoFullName,
                metadata.owner(),
                syncTime,
                latestCommitSha,
                "COMPLETED",
                metadata.lastAnalyzedTimestamp()
        );
        syncMetadataRepository.save(updatedMetadata);

        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        
        int totalUpserted = 1 + commitDocs.size() + prDocs.size() + reviewCount + issueDocs.size() + branchDocs.size() + contributorDocs.size() + releaseDocs.size() + workflowDocs.size();

        log.debug("synchronization completed for {}", repoFullName);
        log.debug("inserted document count: {}", totalUpserted); // Using totalUpserted for simplicity in this PoC
        log.debug("updated document count: 0"); // Spring Data save() acts as upsert natively
        log.debug("skipped document count: 0");
        log.debug("synchronization duration: {} ms", duration.toMillis());
    }
}
