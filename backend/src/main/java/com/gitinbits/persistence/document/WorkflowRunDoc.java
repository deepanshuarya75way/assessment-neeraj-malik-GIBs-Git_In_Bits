package com.gitinbits.persistence.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "workflow_runs")
@CompoundIndex(def = "{'owner': 1, 'repoName': 1}", name = "owner_repo_idx")
public record WorkflowRunDoc(
        @Id String id,        // "owner/repo#name#createdAt"
        String owner,         
        String repoName,      
        String name,
        String status,
        String conclusion,
        @Indexed String actorLogin,
        String createdAt,
        String updatedAt,
        Instant synchronizedAt
) {}
