package com.lnnktrn.timetravel_rainbow.controller.v1;

import com.lnnktrn.timetravel_rainbow.entity.RecordEntity;
import com.lnnktrn.timetravel_rainbow.service.RecordService;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/records")
public class RecordController {

    @Autowired
    private RecordService recordService;

    @GetMapping("/{id}")
    public ResponseEntity<RecordEntity> getRecord(
            @PathVariable @Min(1) Long id
    ) {
        var entity = recordService.getRecord(id);
        return ResponseEntity.ok(entity);
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> upsertRecord(
            @PathVariable @Min(1) Long id,
            @RequestBody String data
    ) {
        recordService.upsertRecord(id, data);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}