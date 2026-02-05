package com.secretlab.kvstore.repo;

import com.secretlab.kvstore.domain.KeyValueRecord;
import com.secretlab.kvstore.repository.KeyValueAggregationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Repository
@RequiredArgsConstructor
public class KeyValueAggregationRepositoryImpl implements KeyValueAggregationRepository {

    private final MongoTemplate mongo;

    @Override
    public List<KeyValueRecord> findLatestPerKey() {
        Aggregation agg = newAggregation(
                sort(Sort.by(Sort.Direction.DESC, "key").and(Sort.by(Sort.Direction.DESC, "version"))),
                group("key")
                        .first("key").as("key")
                        .first("version").as("version")
                        .first("value").as("value")
                        .first("createdAt").as("createdAt"),
                project("key", "version", "value", "createdAt"),
                sort(Sort.by(Sort.Direction.ASC, "key"))
        );

        AggregationResults<KeyValueRecord> res =
                mongo.aggregate(agg, "kv_records", KeyValueRecord.class);

        return res.getMappedResults();
    }
}
