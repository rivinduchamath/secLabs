package com.secretlab.kvstore.repository;

import com.secretlab.kvstore.domain.KeyValueRecord;

import java.util.List;

public interface KeyValueAggregationRepository {
    List<KeyValueRecord> findLatestPerKey();
}
