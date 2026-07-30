package com.gitinbits.persistence.repository;

import com.gitinbits.persistence.document.RepoDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepoRepository extends MongoRepository<RepoDoc, String> {
}
