package com.lnnktrn.timetravel_rainbow.dto;

import java.time.Instant;

public record RecordDto(
        Long id,
        Long version,
        String data,
        Instant createdAt
){}