package com.secretlab.kvstore.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.secretlab.kvstore.domain.KeyCounter;
import com.secretlab.kvstore.domain.KeyValueRecord;
import com.secretlab.kvstore.dto.RecordDto;
import com.secretlab.kvstore.exception.BadRequestException;
import com.secretlab.kvstore.exception.NotFoundException;
import com.secretlab.kvstore.repository.KeyValueAggregationRepository;
import com.secretlab.kvstore.repository.KeyValueRecordRepository;
import com.secretlab.kvstore.service.KeyValueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.data.mongodb.MongoTransactionException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeyValueServiceImpl implements KeyValueService {

    private final MongoTemplate mongo;
    private final KeyValueRecordRepository records;
    private final KeyValueAggregationRepository agg;

    private static final int KEY_MAX = 256;

    @Override
    @Transactional
    @Retryable(
            retryFor = {MongoTransactionException.class, TransientDataAccessException.class},
            maxAttempts = 4,
            backoff = @Backoff(delay = 80, multiplier = 2.0, maxDelay = 1000)
    )
    public RecordDto put(String key, JsonNode value) {
        validateKey(key);
        if (value == null || value.isNull()) throw new BadRequestException("Value must not be null");

        long version = nextVersionAtomic(key);

        Instant now = Instant.now();
        KeyValueRecord rec = KeyValueRecord.builder()
                .key(key)
                .version(version)
                .value(value)
                .createdAt(now)
                .build();

        records.save(rec);

        log.info("Stored key={} version={} at={}", key, version, now);
        return toDto(rec);
    }

    @Override
    public RecordDto getLatest(String key) {
        validateKey(key);
        KeyValueRecord rec = records.findFirstByKeyOrderByVersionDesc(key)
                .orElseThrow(() -> new NotFoundException("Key not found: " + key));
        return toDto(rec);
    }

    @Override
    public RecordDto getAtTimestamp(String key, long unixSeconds) {
        validateKey(key);
        if (unixSeconds <= 0) throw new BadRequestException("timestamp must be a UNIX seconds value > 0");

        Instant ts = Instant.ofEpochSecond(unixSeconds);

        KeyValueRecord rec = records
                .findFirstByKeyAndCreatedAtLessThanEqualOrderByCreatedAtDescVersionDesc(key, ts)
                .orElseThrow(() -> new NotFoundException("No value for key=" + key + " at/before timestamp=" + unixSeconds));

        return toDto(rec);
    }

    @Override
    public List<RecordDto> getAllLatest() {
        return agg.findLatestPerKey().stream().map(this::toDto).toList();
    }

    private long nextVersionAtomic(String key) {
        Query q = new Query(where("_id").is(key)); // _id is KeyCounter.key
        Update u = new Update().inc("seq", 1);

        FindAndModifyOptions opt = FindAndModifyOptions.options()
                .upsert(true)
                .returnNew(true);

        KeyCounter c = mongo.findAndModify(q, u, opt, KeyCounter.class, "kv_counters");
        if (c == null) throw new IllegalStateException("Failed to allocate version for key=" + key);

        return c.getSeq();
    }

    private void validateKey(String key) {
        if (key == null || key.isBlank()) throw new BadRequestException("Key must not be blank");
        if (key.length() > KEY_MAX) throw new BadRequestException("Key too long (max " + KEY_MAX + ")");
    }

    private RecordDto toDto(KeyValueRecord r) {
        return RecordDto.builder()
                .key(r.getKey())
                .version(r.getVersion())
                .timestamp(r.getCreatedAt().getEpochSecond())
                .value(r.getValue())
                .build();
    }
}
