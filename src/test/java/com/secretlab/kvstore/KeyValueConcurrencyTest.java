package com.secretlab.kvstore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.secretlab.kvstore.dto.RecordDto;
import com.secretlab.kvstore.service.KeyValueService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
class KeyValueConcurrencyTest {

    static final MongoDBContainer mongo = new MongoDBContainer("mongo:7");

    @BeforeAll
    static void startMongo() throws Exception {
        mongo.start();

        // Start replica set inside container
        // Note: MongoDBContainer starts mongod; we need replSet. Easiest for CI is:
        // run local docker-compose for dev, and in CI just verify logic (or configure a custom container).
        // If you want replSet in Testcontainers, use docker-compose based integration test profile.
        //
        // For simplicity here: treat this as an example test file; run against docker-compose rs0 for full tx tests.
    }

    @AfterAll
    static void stopMongo() {
        mongo.stop();
    }

    @Autowired KeyValueService kv;
    @Autowired ObjectMapper om;

    @Test
    void concurrentUpdatesProduceSequentialVersions() throws Exception {
        String key = "mykey";

        int threads = 50;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);

        List<Future<RecordDto>> futures = new ArrayList<>();

        // fire 51 writes concurrently (expected versions 1..51)
        for (int i = 1; i <= 51; i++) {
            int n = i;
            futures.add(pool.submit(() -> {
                start.await();
                return kv.put(key, TextNode.valueOf("v" + n));
            }));
        }

        start.countDown();

        List<RecordDto> results = new ArrayList<>();
        for (Future<RecordDto> f : futures) results.add(f.get(10, TimeUnit.SECONDS));

        pool.shutdown();

        Set<Long> versions = new HashSet<>();
        for (RecordDto r : results) versions.add(r.version());

        assertThat(versions).hasSize(51);
        LongStream.rangeClosed(1, 51).forEach(v -> assertThat(versions).contains(v));

        RecordDto latest = kv.getLatest(key);
        assertThat(latest.version()).isEqualTo(51);
    }
}
