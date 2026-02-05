package com.secretlab.kvstore.dto;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record RecordDto(
        @Schema(example = "mykey")
        String key,
        @Schema(example = "1")
        long version,
        @Schema(description = "UNIX timestamp seconds (UTC)", example = "1707140000")
        long timestamp,
        JsonNode value
) {}
