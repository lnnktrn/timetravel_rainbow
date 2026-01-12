package com.lnnktrn.timetravel_rainbow.dto;

import com.fasterxml.jackson.databind.JsonNode;

public record RecordDto(
        Long id,
        JsonNode data
){}