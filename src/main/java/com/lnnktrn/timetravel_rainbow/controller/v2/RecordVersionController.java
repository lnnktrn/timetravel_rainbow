package com.lnnktrn.timetravel_rainbow.controller.v2;

import com.fasterxml.jackson.databind.JsonNode;
import com.lnnktrn.timetravel_rainbow.dto.RecordDto;
import com.lnnktrn.timetravel_rainbow.mapper.EntityToDtoMapper;
import com.lnnktrn.timetravel_rainbow.service.RecordService;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/records")
public class RecordVersionController {
    @Autowired
    private RecordService recordService;

    /**
     * Get record by given id.
     *
     * @param id      - record id
     * @param version (optional) - record version. If not empty, then returns a specific version of a record. If empty - returns latest version.
     * @return record if record exists, 404 otherwise
     * If id<0 or version<0 returns 400
     */
    // GET /api/v2/records/{id}?version={version}
    @GetMapping("/{id}")
    public ResponseEntity<RecordDto> getLatestOrByVersion(
            @PathVariable @Min(1) Long id,
            @RequestParam(required = false) @Min(1) Long version
    ) {
        var entity = (version == null)
                ? recordService.getLatestRecord(id)
                : recordService.getRecord(id, version);

        return ResponseEntity.ok(EntityToDtoMapper.map(entity));
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
    public ResponseEntity<List<RecordDto>> listVersions(@PathVariable @Min(1) Long id) {
        var records = recordService.listVersions(id);
        var dtos = records.stream().map(EntityToDtoMapper::map).toList();
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
    public ResponseEntity<Void> upsertRecord(
            @PathVariable @Min(1) Long id,
            @RequestBody JsonNode data
    ) {
        recordService.upsertRecord(id, data);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
