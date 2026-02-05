package com.secretlab.kvstore.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor @Builder
@Document(collection = "kv_counters")
public class KeyCounter {
    @Id
    private String key;  // stored as _id
    private long seq;    // latest version
}
