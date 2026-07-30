package com.gitinbits.persistence.repository;

import com.gitinbits.persistence.document.PullRequestDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PullRequestRepository extends MongoRepository<PullRequestDoc, String> {
    List<PullRequestDoc> findByOwnerAndRepoName(String owner, String repoName);
}
