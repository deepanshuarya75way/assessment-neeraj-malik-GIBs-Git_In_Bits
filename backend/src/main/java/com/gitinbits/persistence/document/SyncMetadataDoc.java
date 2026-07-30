package com.gitinbits.persistence.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "sync_metadata")
public record SyncMetadataDoc(
        @Id String repoFullName,
        @Indexed String owner,
        Instant lastSyncTimestamp,
        String latestSynchronizedCommitSha,
        String status,
        Instant lastAnalyzedTimestamp
) {}
