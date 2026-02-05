package com.secretlab.kvstore.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.secretlab.kvstore.dto.RecordDto;

import java.util.List;

public interface KeyValueService {
    RecordDto put(String key, JsonNode value);
    RecordDto getLatest(String key);
    RecordDto getAtTimestamp(String key, long unixSeconds);
    List<RecordDto> getAllLatest();
}
