package com.gitinbits.persistence.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "pull_requests")
@CompoundIndex(def = "{'owner': 1, 'repoName': 1}", name = "owner_repo_idx")
public record PullRequestDoc(
        @Id String id,        // "owner/repo#number"
        String owner,         // e.g. "Neeraj-Malik-12"
        String repoName,      // e.g. "DSA-Github"
        @Indexed Integer number,
        String state,
        String title,
        String userLogin,
        boolean draft,
        boolean merged,
        String createdAt,
        String updatedAt,
        String closedAt,
        String mergedAt,
        Integer additions,
        Integer deletions,
        Integer changedFiles,
        Instant synchronizedAt
) {}
