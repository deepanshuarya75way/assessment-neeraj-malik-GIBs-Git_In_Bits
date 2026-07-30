package com.gitinbits.persistence.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "branches")
@CompoundIndex(def = "{'owner': 1, 'repoName': 1}", name = "owner_repo_idx")
public record BranchDoc(
        @Id String id,        // "owner/repo#branchName"
        String owner,         // e.g. "Neeraj-Malik-12"
        String repoName,      // e.g. "DSA-Github"
        @Indexed String name,
        boolean isProtected,
        String commitSha,
        Instant synchronizedAt
) {}
