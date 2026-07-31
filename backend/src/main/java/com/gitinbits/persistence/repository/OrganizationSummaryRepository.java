package com.gitinbits.persistence.repository;

import com.gitinbits.persistence.document.OrganizationSummaryDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationSummaryRepository extends MongoRepository<OrganizationSummaryDoc, String> {
    Optional<OrganizationSummaryDoc> findFirstByOwnerAndTimeframeOrderByGeneratedAtDesc(String owner, String timeframe);
}
