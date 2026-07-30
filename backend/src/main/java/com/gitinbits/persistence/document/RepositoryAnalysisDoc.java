package com.gitinbits.persistence.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "analysis_repositories")
public record RepositoryAnalysisDoc(
        @Id String repoFullName
        // Future deterministic analysis data will be populated here
) {}
