package com.gitinbits.persistence.repository;

import com.gitinbits.persistence.document.BranchDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchRepository extends MongoRepository<BranchDoc, String> {
    List<BranchDoc> findByOwnerAndRepoName(String owner, String repoName);
}
