package com.gitinbits.persistence.repository;

import com.gitinbits.persistence.document.ReleaseDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReleaseRepository extends MongoRepository<ReleaseDoc, String> {
    List<ReleaseDoc> findByOwnerAndRepoName(String owner, String repoName);
}
