package com.gitinbits.persistence.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "commits")
@CompoundIndex(def = "{'owner': 1, 'repoName': 1}", name = "owner_repo_idx")
public record CommitDoc(
        @Id String id,        // "owner/repo#sha"
        String owner,         // e.g. "Neeraj-Malik-12"
        String repoName,      // e.g. "DSA-Github"
        @Indexed String sha,
        String message,
        String authorName,
        String authorEmail,
        String authorDate,
        String committerName,
        String committerEmail,
        String committerDate,
        Integer additions,
        Integer deletions,
        Instant synchronizedAt
) {}
