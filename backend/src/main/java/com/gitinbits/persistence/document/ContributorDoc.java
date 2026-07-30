package com.gitinbits.persistence.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "contributors")
@CompoundIndex(def = "{'owner': 1, 'repoName': 1}", name = "owner_repo_idx")
public record ContributorDoc(
        @Id String id,        // "owner/repo#login"
        String owner,         // e.g. "Neeraj-Malik-12"
        String repoName,      // e.g. "DSA-Github"
        @Indexed String login,
        Integer contributions,
        String avatarUrl,
        String htmlUrl,
        Instant synchronizedAt
) {}
