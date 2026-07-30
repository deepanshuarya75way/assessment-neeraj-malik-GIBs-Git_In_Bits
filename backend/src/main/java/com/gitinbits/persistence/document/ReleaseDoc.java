package com.gitinbits.persistence.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "releases")
@CompoundIndex(def = "{'owner': 1, 'repoName': 1}", name = "owner_repo_idx")
public record ReleaseDoc(
        @Id String id,        // "owner/repo#version"
        String owner,         // e.g. "Neeraj-Malik-12"
        String repoName,      // e.g. "DSA-Github"
        String version,
        String tag,
        String name,
        boolean draft,
        boolean prerelease,
        String publishedAt,
        String author,
        Instant synchronizedAt
) {}
