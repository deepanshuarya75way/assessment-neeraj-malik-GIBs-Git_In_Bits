package com.gitinbits.persistence.repository;

import com.gitinbits.persistence.document.CommitDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommitRepository extends MongoRepository<CommitDoc, String> {
    List<CommitDoc> findByOwnerAndRepoName(String owner, String repoName);
    List<CommitDoc> findTop10ByOwnerAndRepoNameOrderByAuthorDateDesc(String owner, String repoName);
    CommitDoc findFirstByOwnerAndRepoNameOrderByAuthorDateAsc(String owner, String repoName);
    long countByOwnerAndRepoName(String owner, String repoName);
}
