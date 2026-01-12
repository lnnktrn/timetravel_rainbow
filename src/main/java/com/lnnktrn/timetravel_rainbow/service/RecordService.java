package com.lnnktrn.timetravel_rainbow.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lnnktrn.timetravel_rainbow.entity.LatestVersionEntity;
import com.lnnktrn.timetravel_rainbow.entity.RecordEntity;
import com.lnnktrn.timetravel_rainbow.entity.RecordId;
import com.lnnktrn.timetravel_rainbow.exception.NoSuchRecordException;
import com.lnnktrn.timetravel_rainbow.json.JsonMergePatchUtil;
import com.lnnktrn.timetravel_rainbow.repository.LatestVersionRepository;
import com.lnnktrn.timetravel_rainbow.repository.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;

@Service
public class RecordService {

    @Autowired
    private RecordRepository recordRepository;
    @Autowired
    private LatestVersionRepository latestVersionRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JsonMergePatchUtil jsonMergePatchUtil;

    public RecordEntity getLatestRecord(Long id) {
        return latestVersionRepository.findLatestRecordById(id)
                .orElseThrow(() -> new NoSuchRecordException(id));
    }

    public RecordEntity getRecord(Long id, Long version) {
        RecordId recordId = RecordId.builder().id(id).version(version).build();
        return recordRepository.findById(recordId)
                .orElseThrow(() -> new NoSuchRecordException(id, version));
    }

    public List<RecordEntity> listVersions(Long id) {
        List<RecordEntity> entities = recordRepository.findAllByRecordId_IdOrderByRecordId_VersionAsc(id);
        if (entities.isEmpty()) {
            throw new NoSuchRecordException(id);
        }
        return entities;
    }

    public RecordEntity getRecordAt(Long id, Instant at) {
        return recordRepository.findRecordsAt(id, at, PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElseThrow(() -> new NoSuchRecordException(id, at));
    }

    public RecordEntity upsertRecord(Long id, JsonNode patch) {
        var latestOpt = latestVersionRepository.findByIdForUpdate(id);

        long newVersion;
        JsonNode baseData;
        LatestVersionEntity latest;

        if (latestOpt.isPresent()) {
            latest = latestOpt.get();
            baseData = getRecord(id, latest.getVersion()).getData();
            newVersion = latest.getVersion() + 1;
        } else {
            baseData = objectMapper.createObjectNode();
            newVersion = 1;
            latest = LatestVersionEntity.builder().id(id).version(newVersion).build();
        }

        JsonNode newData = jsonMergePatchUtil.applyMergePatch(baseData, patch);

        var saved = recordRepository.save(
                RecordEntity.builder()
                        .recordId(RecordId.builder().id(id).version(newVersion).build())
                        .data(newData)
                        .build()
        );

        latest.setVersion(newVersion);
        latestVersionRepository.save(latest);

        return saved;
    }

}
