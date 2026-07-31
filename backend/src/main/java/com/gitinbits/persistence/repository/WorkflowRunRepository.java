package com.gitinbits.persistence.repository;

import com.gitinbits.persistence.document.WorkflowRunDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowRunRepository extends MongoRepository<WorkflowRunDoc, String> {
    List<WorkflowRunDoc> findByOwnerAndRepoName(String owner, String repoName);
    long countByOwnerAndActorLoginAndConclusion(String owner, String actorLogin, String conclusion);
    List<WorkflowRunDoc> findByOwnerAndUpdatedAtBetweenOrderByUpdatedAtDesc(String owner, String start, String end);
    List<WorkflowRunDoc> findByOwnerAndActorLoginAndUpdatedAtBetweenOrderByUpdatedAtDesc(String owner, String actorLogin, String start, String end);
}
