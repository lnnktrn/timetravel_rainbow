package com.lnnktrn.timetravel_rainbow.controller.v1;

import com.fasterxml.jackson.databind.JsonNode;
import com.lnnktrn.timetravel_rainbow.dto.RecordDto;
import com.lnnktrn.timetravel_rainbow.mapper.EntityToDtoMapper;
import com.lnnktrn.timetravel_rainbow.service.RecordService;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/records")
@Validated
public class RecordController {

    @Autowired
    private RecordService recordService;

    /**
     * Get the latest record version by given id.
     *
     * @param id      - record id
     * @return record if record exists, 404 otherwise
     * If id<0 returns 400
     */
    @GetMapping("/{id}")
    public ResponseEntity<RecordDto> getRecord(
            @PathVariable @Min(1) Long id
    ) {
        var entity = recordService.getLatestRecord(id);
        return ResponseEntity.ok(EntityToDtoMapper.mapRecordEntityToRecorDto(entity));
    }

    /**
     * Creates a new version of a record with given record data.
     * If a record does not exist, it will be created with version 1.
     *
     * @param id      - record id
     * @param data - record data
     */
    @PostMapping("/{id}")
    public ResponseEntity<RecordDto> upsertRecord(
            @PathVariable @Min(1) Long id,
            @RequestBody JsonNode data
    ) {
        var entity = recordService.upsertRecord(id, data);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(EntityToDtoMapper.mapRecordEntityToRecorDto(entity));
    }
}