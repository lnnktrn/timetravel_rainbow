package com.lnnktrn.timetravel_rainbow.controller.v2;

import com.lnnktrn.timetravel_rainbow.dto.RecordDto;
import com.lnnktrn.timetravel_rainbow.mapper.EntityToDtoMapper;
import com.lnnktrn.timetravel_rainbow.service.RecordService;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     * @return entityDto if record exists, 404 otherwise
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
}
