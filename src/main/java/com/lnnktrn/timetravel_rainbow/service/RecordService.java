package com.lnnktrn.timetravel_rainbow.service;

import com.lnnktrn.timetravel_rainbow.entity.RecordEntity;
import com.lnnktrn.timetravel_rainbow.entity.RecordId;
import com.lnnktrn.timetravel_rainbow.exception.NoSuchRecordException;
import com.lnnktrn.timetravel_rainbow.repository.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecordService {

    @Autowired
    private RecordRepository recordRepository;

    public RecordEntity getRecord(Long id) {
        return recordRepository.findById(RecordId.builder().id(id).build())
                .orElseThrow(() -> new NoSuchRecordException(id));
    }

    public void upsertRecord(Long id, String data) {
        RecordEntity existingRecord = recordRepository.findById(RecordId.builder().id(id).build())
                .orElseGet(() -> RecordEntity.builder()
                        .recordId(RecordId.builder().id(id).version(1L).build()).data("{}")
                        .build());
        existingRecord.setData(data);
        recordRepository.save(existingRecord);
    }

}
