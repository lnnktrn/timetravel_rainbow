package com.lnnktrn.timetravel_rainbow.controller.v2;

import com.fasterxml.jackson.databind.JsonNode;
import com.lnnktrn.timetravel_rainbow.dto.RecordVersionDto;
import com.lnnktrn.timetravel_rainbow.entity.RecordEntity;
import com.lnnktrn.timetravel_rainbow.mapper.EntityToDtoMapper;
import com.lnnktrn.timetravel_rainbow.service.RecordService;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v2/records")
@Validated
public class RecordVersionController {
    @Autowired
    private RecordService recordService;

    /**
     * Get record by given id.
     *  By default, returns the latest version of the record.
     *  Optionally, a specific version or the state of the record at a given moment in time
     *  can be requested using query parameters.
     *
     *  GET /api/v2/records/{id} – returns the latest version</li>
     *  GET /api/v2/records/{id}?version={version} – returns a specific version</li>
     *  GET /api/v2/records/{id}?at={timestamp} – returns the version valid at the given moment</li>
     *
     * @param id      - record id
     * @param version (optional) - the exact version of the record to retrieve; must be greater than or equal to 1
     * @param at (optional) - the point in time for which the record state should be returned; must not be used together with version
     *
     * @throws IllegalArgumentException if both version and at parameters are provided
     *
     * @response 200 OK if the record (or requested version) exists
     * @response 400 Bad Request if request parameters are invalid or mutually exclusive
     * @response 404 Not Found if the record does not exist at the requested time or version
     */
    // GET /api/v2/records/{id}?version={version}
    @GetMapping("/{id}")
    public ResponseEntity<RecordVersionDto> getRecord(
            @PathVariable @Min(1) Long id,
            @RequestParam(required = false) @Min(1) Long version,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant at
    ) {
        RecordEntity entity;
        if (version != null && at != null) {
            throw new IllegalArgumentException("Specify either version or at, not both");
        }
        if (version != null) {
            entity = recordService.getRecord(id, version);
        } else if (at != null) {
            entity = recordService.getRecordAt(id, at);
        } else {
            entity = recordService.getLatestRecord(id);
        }

        return ResponseEntity.ok(EntityToDtoMapper.mapRecorEntityToRecorVersionDto(entity));
    }

    /**
     * Get all record versions for given id.
     *
     * @param id - record id
     * @return List of records if records exist, 404 otherwise
     * If id<0 returns 400
     */
    // GET /api/v2/records/{id}/history
    @GetMapping(value = "/{id}/history")
    public ResponseEntity<List<RecordVersionDto>> listVersions(@PathVariable @Min(1) Long id) {
        var records = recordService.listVersions(id);
        var dtos = records.stream().map(EntityToDtoMapper::mapRecorEntityToRecorVersionDto).toList();
        return ResponseEntity.ok(dtos);
    }

    /**
     * Creates a new version of a record with given record data.
     * If a record does not exist, it will be created with version 1.
     *
     * @param id      - record id
     * @param data - record data
     */
    // Apply updates to the latest version while preserving history
    // POST /api/v2/records/{id}
    @PostMapping("/{id}")
    public ResponseEntity<RecordVersionDto> upsertRecord(
            @PathVariable @Min(1) Long id,
            @RequestBody JsonNode data
    ) {
        var entity = recordService.upsertRecord(id, data);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(EntityToDtoMapper.mapRecorEntityToRecorVersionDto(entity));
    }

}
