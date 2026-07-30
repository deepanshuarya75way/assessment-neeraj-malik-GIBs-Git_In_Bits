package com.gitinbits.persistence.repository;

import com.gitinbits.persistence.document.SyncMetadataDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SyncMetadataRepository extends MongoRepository<SyncMetadataDoc, String> {
}
