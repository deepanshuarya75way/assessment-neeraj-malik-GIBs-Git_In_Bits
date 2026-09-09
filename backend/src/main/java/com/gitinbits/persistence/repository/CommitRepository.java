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
    
    // Cross-repo aggregation methods
    List<CommitDoc> findByOwnerAndAuthorNameOrderByAuthorDateDesc(String owner, String authorName);
    CommitDoc findFirstByOwnerAndAuthorNameOrderByAuthorDateDesc(String owner, String authorName);
    List<CommitDoc> findByOwnerOrderByAuthorDateDesc(String owner);
    List<CommitDoc> findByOwnerAndAuthorDateBetweenOrderByAuthorDateDesc(String owner, String start, String end);
    List<CommitDoc> findByOwnerAndAuthorNameAndAuthorDateBetweenOrderByAuthorDateDesc(String owner, String authorName, String start, String end);
    
    @org.springframework.data.mongodb.repository.Query(value = "{ 'owner': ?0, '$or': [ { 'authorName': ?1 }, { 'githubLogin': ?1 } ], 'authorDate': { '$gte': ?2, '$lte': ?3 } }", sort = "{ 'authorDate': -1 }")
    List<CommitDoc> findDeveloperCommits(String owner, String identifier, String start, String end);
    
    @org.springframework.data.mongodb.repository.Aggregation(pipeline = {
        "{ '$match': { 'owner': ?0 } }",
        "{ '$sort': { 'authorDate': -1 } }",
        "{ '$group': { '_id': '$authorName', 'commitCount': { '$sum': 1 }, 'latestCommitDate': { '$first': '$authorDate' }, 'latestCommitMessage': { '$first': '$message' }, 'githubLogin': { '$first': '$githubLogin' } } }",
        "{ '$sort': { 'commitCount': -1 } }"
    })
    List<com.gitinbits.dto.response.repo.DeveloperActivitySummaryDto> getDeveloperActivitySummary(String owner);
}
