package com.secretlab.kvstore.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.secretlab.kvstore.dto.RecordDto;
import com.secretlab.kvstore.exception.BadRequestException;
import com.secretlab.kvstore.service.KeyValueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/object")
public class ObjectController {

    private final KeyValueService kv;

    @Operation(
            summary = "Create/Update key with versioning (+1 each write)",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(name="jsonValue", value="{\"mykey\": {\"foo\":\"bar\"}}"),
                                    @ExampleObject(name="stringValue", value="{\"mykey\": \"value1\"}")
                            }
                    )
            ),
            responses = @ApiResponse(responseCode = "200", description = "Stored record")
    )
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public RecordDto put(@RequestBody Map<String, JsonNode> body) {
        if (body == null || body.isEmpty()) throw new BadRequestException("Body must contain exactly one key");
        if (body.size() != 1) throw new BadRequestException("Body must contain exactly one key (e.g. {\"mykey\": ...})");

        Map.Entry<String, JsonNode> e = body.entrySet().iterator().next();
        log.debug("PUT key={} valueType={}", e.getKey(), e.getValue() == null ? "null" : e.getValue().getNodeType());
        return kv.put(e.getKey(), e.getValue());
    }

    @Operation(summary = "Get latest value for a key (or value at timestamp if provided)")
    @GetMapping(value = "/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public RecordDto get(
            @PathVariable String key,
            @RequestParam(name = "timestamp", required = false) Long timestamp
    ) {
        return (timestamp == null) ? kv.getLatest(key) : kv.getAtTimestamp(key, timestamp);
    }

    @Operation(summary = "Get all latest records (latest value per key)")
    @GetMapping(value = "/get_all_records", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RecordDto> getAll() {
        return kv.getAllLatest();
    }
}
