package com.gitinbits.persistence.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "repositories")
@CompoundIndex(def = "{'owner': 1, 'repoName': 1}", name = "owner_repo_idx")
public record RepoDoc(
        @Id String id,        // "owner/repoName" e.g. "Neeraj-Malik-12/DSA-Github"
        @Indexed String owner,         // e.g. "Neeraj-Malik-12"
        String repoName,               // e.g. "DSA-Github"  (replaces the old 'name' field)
        String fullName,
        String description,
        boolean isPrivate,
        boolean isFork,
        String defaultBranch,
        int stargazersCount,
        int watchersCount,
        int forksCount,
        int openIssuesCount,
        String language,
        String createdAt,
        String updatedAt,
        String pushedAt,
        Instant synchronizedAt
) {}
