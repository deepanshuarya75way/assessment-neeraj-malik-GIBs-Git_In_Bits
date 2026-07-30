package com.gitinbits.persistence.repository;

import com.gitinbits.persistence.document.ContributorDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContributorRepository extends MongoRepository<ContributorDoc, String> {
    List<ContributorDoc> findByOwnerAndRepoName(String owner, String repoName);
}
