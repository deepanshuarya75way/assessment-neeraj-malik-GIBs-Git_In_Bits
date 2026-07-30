package com.gitinbits.persistence.repository;

import com.gitinbits.persistence.document.RepositoryAnalysisDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryAnalysisRepository extends MongoRepository<RepositoryAnalysisDoc, String> {
}
