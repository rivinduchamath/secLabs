package com.secretlab.kvstore.domain;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "kv_records")
@CompoundIndex(name = "uk_key_version", def = "{'key': 1, 'version': 1}", unique = true)
public class KeyValueRecord {

    @Id
    private ObjectId id;

    private String key;

    private long version;

    private JsonNode value;

    private Instant createdAt; // UTC
}
