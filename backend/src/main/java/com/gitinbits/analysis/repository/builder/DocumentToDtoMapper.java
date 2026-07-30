package com.gitinbits.analysis.repository.builder;

import com.gitinbits.dto.response.repo.*;
import com.gitinbits.persistence.document.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DocumentToDtoMapper {

    public RepoDto toRepoDto(RepoDoc doc) {
        if (doc == null) return null;
        return new RepoDto(
                doc.repoName(),
                doc.fullName(),
                doc.description(),
                doc.isPrivate() ? "private" : "public",
                doc.defaultBranch(),
                doc.language(),
                null, // htmlUrl
                null, // cloneUrl
                null, // sshUrl
                doc.isFork(),
                false, // archived
                List.of(), // topics
                doc.openIssuesCount(),
                doc.forksCount(),
                doc.stargazersCount(),
                doc.watchersCount(),
                0, // sizeKb
                doc.createdAt(),
                doc.updatedAt(),
                doc.pushedAt(),
                null, // homepage
                0, // networkCount
                0, // subscribersCount
                null // licenseName
        );
    }

    public CommitDto toCommitDto(CommitDoc doc) {
        return new CommitDto(
                doc.sha(),
                doc.authorName(),
                doc.authorEmail(),
                doc.committerName(),
                doc.committerEmail(),
                doc.message(),
                doc.authorDate(),
                null, // url
                List.of(), // parentShas
                null, // verified
                null, // verificationReason
                0, // filesChangedCount
                0, // additions
                0, // deletions
                0, // totalChanges
                List.of() // changedFileNames
        );
    }

    public PullRequestDto toPullRequestDto(PullRequestDoc doc) {
        return new PullRequestDto(
                doc.number(),
                doc.title(),
                null, // description
                doc.userLogin(),
                doc.state(),
                null, // locked
                doc.draft(),
                doc.createdAt(),
                doc.updatedAt(),
                doc.closedAt(),
                doc.mergedAt(),
                null, // mergeCommitSha
                null, // mergeableState
                List.of(), // assignees
                List.of(), // requestedReviewers
                List.of(), // labels
                null, // milestone
                0, // commitsCount
                doc.changedFiles(),
                doc.additions(),
                doc.deletions(),
                0, // reviewCount
                0, // commentCount
                null, // baseBranch
                null // headBranch
        );
    }

    public IssueDto toIssueDto(IssueDoc doc) {
        return new IssueDto(
                doc.number(),
                doc.title(),
                null, // description
                List.of(), // labels
                List.of(), // assignees
                null, // milestone
                doc.state(),
                null, // locked
                doc.comments(),
                doc.createdAt(),
                doc.updatedAt(),
                doc.closedAt()
        );
    }

    public ReviewDto toReviewDto(ReviewDoc doc) {
        return new ReviewDto(
                doc.reviewer(),
                doc.state(),
                doc.submittedAt(),
                null, // body
                null // commitSha
        );
    }

    public BranchDto toBranchDto(BranchDoc doc) {
        return new BranchDto(
                doc.name(),
                doc.isProtected(),
                doc.commitSha(),
                null, // latestCommitTimestamp
                null, // latestCommitAuthor
                null, // requiredStatusChecks
                null, // requiredReviews
                null, // forcePushAllowed
                null // deletionAllowed
        );
    }

    public ContributorDto toContributorDto(ContributorDoc doc) {
        return new ContributorDto(
                doc.login(),
                doc.avatarUrl(),
                doc.htmlUrl(),
                doc.contributions()
        );
    }
}
