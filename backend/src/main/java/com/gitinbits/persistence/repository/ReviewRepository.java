package com.gitinbits.persistence.repository;

import com.gitinbits.persistence.document.ReviewDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends MongoRepository<ReviewDoc, String> {
    List<ReviewDoc> findByOwnerAndRepoName(String owner, String repoName);
}
