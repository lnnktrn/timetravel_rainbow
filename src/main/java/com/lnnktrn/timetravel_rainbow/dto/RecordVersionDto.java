package com.lnnktrn.timetravel_rainbow.dto;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;

public record RecordVersionDto(
        Long id,
        Long version,
        JsonNode data,
        Instant createdAt
){}