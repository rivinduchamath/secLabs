package com.secretlab.kvstore.repository;

import com.secretlab.kvstore.domain.KeyValueRecord;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.Optional;

public interface KeyValueRecordRepository extends MongoRepository<KeyValueRecord, ObjectId> {

    Optional<KeyValueRecord> findFirstByKeyOrderByVersionDesc(String key);

    Optional<KeyValueRecord> findFirstByKeyAndCreatedAtLessThanEqualOrderByCreatedAtDescVersionDesc(String key, Instant ts);
}
