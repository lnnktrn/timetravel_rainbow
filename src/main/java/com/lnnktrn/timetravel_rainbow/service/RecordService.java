package com.lnnktrn.timetravel_rainbow.service;

import com.lnnktrn.timetravel_rainbow.entity.LatestVersionEntity;
import com.lnnktrn.timetravel_rainbow.entity.RecordEntity;
import com.lnnktrn.timetravel_rainbow.entity.RecordId;
import com.lnnktrn.timetravel_rainbow.exception.NoSuchRecordException;
import com.lnnktrn.timetravel_rainbow.repository.LatestVersionRepository;
import com.lnnktrn.timetravel_rainbow.repository.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecordService {

    @Autowired
    private RecordRepository recordRepository;
    @Autowired
    private LatestVersionRepository latestVersionRepository;

    public RecordEntity getLatestRecord(Long id) {
        return latestVersionRepository.findLatestRecordById(id)
                .orElseThrow(() -> new NoSuchRecordException(id));
    }

    public RecordEntity getRecord(Long id, Long version) {
        RecordId recordId = RecordId.builder().id(id).version(version).build();
        return recordRepository.findById(recordId)
                .orElseThrow(() -> new NoSuchRecordException(id, version));
    }

    public void upsertRecord(Long id, String data) {
        RecordEntity existingRecord = recordRepository.findById(RecordId.builder().id(id).build())
                .orElseGet(() -> RecordEntity.builder()
                        .recordId(RecordId.builder().id(id).version(1L).build()).data("{}")
                        .build());
        existingRecord.setData(data);
        recordRepository.save(existingRecord);
        latestVersionRepository.save(LatestVersionEntity.builder()
                        .id(existingRecord.getRecordId().getId())
                        .version(existingRecord.getRecordId().getVersion())
                .build());
    }

}
