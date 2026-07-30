package com.gitinbits.persistence.repository;

import com.gitinbits.persistence.document.OrganizationAnalysisDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationAnalysisRepository extends MongoRepository<OrganizationAnalysisDoc, String> {
}
