package com.gitinbits.sync;

import com.gitinbits.dto.response.repo.*;
import com.gitinbits.persistence.document.*;
import org.springframework.stereotype.Component;
import java.time.Instant;

/**
 * Maps raw GitHub DTOs to MongoDB documents.
 *
 * <p>Each document stores {@code owner} and {@code repoName} as separate,
 * independently indexed fields. This enables efficient compound-index queries
 * like {@code findByOwnerAndRepoName} without scanning the entire collection.
 *
 * <p>The document ID convention is {@code "owner/repoName#<unique-key>"}
 * to guarantee uniqueness across all owners and repos.
 */
@Component
public class GithubToDocumentMapper {

    /**
     * Splits a "owner/repoName" full-name into its two parts.
     * Returns a 2-element array: [owner, repoName].
     */
    private String[] splitFullName(String repoFullName) {
        String[] parts = repoFullName.split("/", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid repoFullName format (expected 'owner/repo'): " + repoFullName);
        }
        return parts;
    }

    public RepoDoc toRepoDoc(RepoDto dto, String repoFullName, Instant syncTime) {
        String[] parts = splitFullName(repoFullName);
        String owner    = parts[0];
        String repoName = parts[1];
        return new RepoDoc(
                repoFullName,   // id = "owner/repoName"
                owner,
                repoName,
                repoFullName,
                dto.description(),
                "private".equalsIgnoreCase(dto.visibility()),
                dto.fork(),
                dto.defaultBranch(),
                dto.stargazersCount(),
                dto.watchersCount(),
                dto.forksCount(),
                dto.openIssuesCount(),
                dto.language(),
                dto.createdAt(),
                dto.updatedAt(),
                dto.pushedAt(),
                syncTime
        );
    }

    public CommitDoc toCommitDoc(CommitDto dto, String repoFullName, Instant syncTime) {
        String[] parts  = splitFullName(repoFullName);
        String owner    = parts[0];
        String repoName = parts[1];
        String sha      = dto.sha();
        String id       = repoFullName + "#" + sha;
        return new CommitDoc(
                id,
                owner,
                repoName,
                sha,
                dto.message(),
                dto.authorName(),
                dto.authorEmail(),
                dto.timestamp(),
                dto.committerName(),
                dto.committerEmail(),
                dto.timestamp(),
                syncTime
        );
    }

    public PullRequestDoc toPullRequestDoc(PullRequestDto dto, String repoFullName, Instant syncTime) {
        String[] parts  = splitFullName(repoFullName);
        String owner    = parts[0];
        String repoName = parts[1];
        String id       = repoFullName + "#" + dto.number();
        return new PullRequestDoc(
                id,
                owner,
                repoName,
                dto.number(),
                dto.state(),
                dto.title(),
                dto.author(),
                dto.draft() != null ? dto.draft() : false,
                dto.mergedAt() != null,
                dto.createdAt(),
                dto.updatedAt(),
                dto.closedAt(),
                dto.mergedAt(),
                dto.additions(),
                dto.deletions(),
                dto.changedFilesCount(),
                syncTime
        );
    }

    public IssueDoc toIssueDoc(IssueDto dto, String repoFullName, Instant syncTime) {
        String[] parts  = splitFullName(repoFullName);
        String owner    = parts[0];
        String repoName = parts[1];
        String id       = repoFullName + "#" + dto.number();
        return new IssueDoc(
                id,
                owner,
                repoName,
                dto.number(),
                dto.state(),
                dto.title(),
                null,
                dto.createdAt(),
                dto.updatedAt(),
                dto.closedAt(),
                dto.commentCount(),
                syncTime
        );
    }

    public ReviewDoc toReviewDoc(ReviewDto dto, String repoFullName, Integer prNumber, Instant syncTime) {
        String[] parts  = splitFullName(repoFullName);
        String owner    = parts[0];
        String repoName = parts[1];
        String id       = repoFullName + "#" + prNumber + "#" + dto.reviewer() + "#" + dto.submittedAt();
        return new ReviewDoc(
                id,
                owner,
                repoName,
                prNumber,
                dto.state(),
                dto.reviewer(),
                dto.submittedAt(),
                syncTime
        );
    }

    public BranchDoc toBranchDoc(BranchDto dto, String repoFullName, Instant syncTime) {
        String[] parts  = splitFullName(repoFullName);
        String owner    = parts[0];
        String repoName = parts[1];
        String id       = repoFullName + "#" + dto.name();
        return new BranchDoc(
                id,
                owner,
                repoName,
                dto.name(),
                dto.isProtected(),
                dto.latestCommitSha(),
                syncTime
        );
    }

    public ContributorDoc toContributorDoc(ContributorDto dto, String repoFullName, Instant syncTime) {
        String[] parts  = splitFullName(repoFullName);
        String owner    = parts[0];
        String repoName = parts[1];
        String id       = repoFullName + "#" + dto.username();
        return new ContributorDoc(
                id,
                owner,
                repoName,
                dto.username(),
                dto.contributions(),
                dto.avatarUrl(),
                dto.profileUrl(),
                syncTime
        );
    }

    public ReleaseDoc toReleaseDoc(ReleaseDto dto, String repoFullName, Instant syncTime) {
        String[] parts  = splitFullName(repoFullName);
        String owner    = parts[0];
        String repoName = parts[1];
        String id       = repoFullName + "#" + dto.version();
        return new ReleaseDoc(
                id,
                owner,
                repoName,
                dto.version(),
                dto.tag(),
                dto.name(),
                dto.draft(),
                dto.prerelease(),
                dto.publishedAt(),
                dto.author(),
                syncTime
        );
    }
}
