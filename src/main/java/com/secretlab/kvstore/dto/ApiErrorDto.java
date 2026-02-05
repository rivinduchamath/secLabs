package com.secretlab.kvstore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ApiErrorDto(
        @Schema(example = "1707140000")
        long timestamp,
        @Schema(example = "/object/mykey")
        String path,
        @Schema(example = "NOT_FOUND")
        String code,
        @Schema(example = "Key not found: mykey")
        String message
) {}
