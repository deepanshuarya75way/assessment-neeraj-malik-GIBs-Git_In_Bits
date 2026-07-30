package com.gitinbits.persistence.repository;

import com.gitinbits.persistence.document.IssueDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueRepository extends MongoRepository<IssueDoc, String> {
    List<IssueDoc> findByOwnerAndRepoName(String owner, String repoName);
}
