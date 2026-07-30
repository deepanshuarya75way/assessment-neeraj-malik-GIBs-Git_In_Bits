package com.gitinbits.persistence.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "analysis_organizations")
public record OrganizationAnalysisDoc(
        @Id String organizationName
        // Future deterministic analysis data will be populated here
) {}
