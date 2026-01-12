package com.lnnktrn.timetravel_rainbow.controller.v1;

import com.lnnktrn.timetravel_rainbow.entity.RecordEntity;
import com.lnnktrn.timetravel_rainbow.repository.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/records")
public class RecordController {

    @Autowired
    private RecordRepository repo;

    @GetMapping("/{id}")
    public ResponseEntity<RecordEntity> getRecord(@PathVariable Long id) {
        return repo.findById(id)
                .map(record -> ResponseEntity.ok(record))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> upsertRecord(
            @PathVariable Long id,
            @RequestBody String data
    ) {
        RecordEntity existingRecord = repo.findById(id)
                .orElseGet(() -> RecordEntity.builder().id(id).data("{}").build());
        existingRecord.setData(data);
        repo.save(existingRecord);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}