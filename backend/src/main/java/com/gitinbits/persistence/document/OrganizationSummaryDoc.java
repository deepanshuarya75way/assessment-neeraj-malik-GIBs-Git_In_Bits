package com.gitinbits.persistence.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "organization_summaries")
public record OrganizationSummaryDoc(
    @Id String id,
    String owner,
    String timeframe,
    String summaryText,
    Instant generatedAt
) {}
