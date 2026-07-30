package com.gitinbits.persistence.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "reviews")
@CompoundIndex(def = "{'owner': 1, 'repoName': 1}", name = "owner_repo_idx")
public record ReviewDoc(
        @Id String id,        // "owner/repo#prNumber#reviewer#submittedAt"
        String owner,         // e.g. "Neeraj-Malik-12"
        String repoName,      // e.g. "DSA-Github"
        @Indexed Integer prNumber,
        String state,
        String reviewer,
        String submittedAt,
        Instant synchronizedAt
) {}
