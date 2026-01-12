package com.lnnktrn.timetravel_rainbow.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lnnktrn.timetravel_rainbow.entity.LatestVersionEntity;
import com.lnnktrn.timetravel_rainbow.entity.RecordEntity;
import com.lnnktrn.timetravel_rainbow.entity.RecordId;
import com.lnnktrn.timetravel_rainbow.exception.NoSuchRecordException;
import com.lnnktrn.timetravel_rainbow.json.JsonMergePatchUtil;
import com.lnnktrn.timetravel_rainbow.repository.LatestVersionRepository;
import com.lnnktrn.timetravel_rainbow.repository.RecordRepository;
import com.lnnktrn.timetravel_rainbow.util.RecordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class RecordServiceTest {

    @Mock
    RecordRepository recordRepository;

    @Mock
    LatestVersionRepository latestVersionRepository;

    @Mock
    ObjectMapper objectMapper;

    @Mock
    JsonMergePatchUtil jsonMergePatchUtil;

    @InjectMocks
    private RecordService recordService;

    private ObjectMapper realMapper;
    private RecordUtil recordUtil;

    @BeforeEach
    void setup() {
        // Helpful for building JsonNodes easily
        realMapper = new ObjectMapper();
        recordUtil = new RecordUtil(new ObjectMapper());
    }

    // -------------------- getLatestRecord --------------------

    @Test
    void getLatestRecord_shouldReturnEntity_whenExists() {
        long id = 1L;
        RecordEntity entity = recordUtil.makeEntity(id, 3L, "{\"k\":\"v\"}", Instant.now());

        when(latestVersionRepository.findLatestRecordById(id)).thenReturn(Optional.of(entity));

        RecordEntity result = recordService.getLatestRecord(id);

        assertSame(entity, result);
        verify(latestVersionRepository).findLatestRecordById(id);
        verifyNoMoreInteractions(latestVersionRepository);
        verifyNoInteractions(recordRepository, objectMapper, jsonMergePatchUtil);
    }

    @Test
    void getLatestRecord_shouldThrow_whenMissing() {
        long id = 42L;
        when(latestVersionRepository.findLatestRecordById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchRecordException.class, () -> recordService.getLatestRecord(id));

        verify(latestVersionRepository).findLatestRecordById(id);
        verifyNoMoreInteractions(latestVersionRepository);
        verifyNoInteractions(recordRepository, objectMapper, jsonMergePatchUtil);
    }

    // -------------------- getRecord --------------------

    @Test
    void getRecordByVersion_shouldReturnEntity_whenExists() {
        long id = 5L;
        long version = 7L;

        RecordId recordId = RecordId.builder().id(id).version(version).build();
        RecordEntity entity = recordUtil.makeEntity(id, version, "{\"k\":\"v\"}", Instant.now());

        when(recordRepository.findById(recordId)).thenReturn(Optional.of(entity));

        RecordEntity result = recordService.getRecord(id, version);

        assertSame(entity, result);
        verify(recordRepository).findById(recordId);
        verifyNoMoreInteractions(recordRepository);
        verifyNoInteractions(latestVersionRepository, objectMapper, jsonMergePatchUtil);
    }

    @Test
    void getRecordByVersion_shouldThrow_whenMissing() {
        long id = 5L;
        long version = 7L;

        RecordId recordId = RecordId.builder().id(id).version(version).build();
        when(recordRepository.findById(recordId)).thenReturn(Optional.empty());

        assertThrows(NoSuchRecordException.class, () -> recordService.getRecord(id, version));

        verify(recordRepository).findById(recordId);
        verifyNoMoreInteractions(recordRepository);
        verifyNoInteractions(latestVersionRepository, objectMapper, jsonMergePatchUtil);
    }

    // -------------------- listVersions --------------------

    @Test
    void listVersions_shouldReturnList_whenNotEmpty() {
        long id = 10L;

        List<RecordEntity> list = List.of(
                recordUtil.makeEntity(id, 1L, "{\"k\":\"v1\"}", Instant.now()),
                recordUtil.makeEntity(id, 2L, "{\"k\":\"v2\"}", Instant.now())
        );

        when(recordRepository.findAllByRecordId_IdOrderByRecordId_VersionAsc(id)).thenReturn(list);

        List<RecordEntity> result = recordService.listVersions(id);

        assertSame(list, result);
        verify(recordRepository).findAllByRecordId_IdOrderByRecordId_VersionAsc(id);
        verifyNoMoreInteractions(recordRepository);
        verifyNoInteractions(latestVersionRepository, objectMapper, jsonMergePatchUtil);
    }

    @Test
    void listVersions_shouldThrow_whenEmpty() {
        long id = 10L;
        when(recordRepository.findAllByRecordId_IdOrderByRecordId_VersionAsc(id)).thenReturn(List.of());

        assertThrows(NoSuchRecordException.class, () -> recordService.listVersions(id));

        verify(recordRepository).findAllByRecordId_IdOrderByRecordId_VersionAsc(id);
        verifyNoMoreInteractions(recordRepository);
        verifyNoInteractions(latestVersionRepository, objectMapper, jsonMergePatchUtil);
    }

    // -------------------- getRecordAt --------------------

    @Test
    void getRecordAt_shouldReturnFirstResult_whenFound() {
        long id = 1L;
        Instant at = Instant.parse("2025-01-12T10:15:30Z");

        RecordEntity entity = recordUtil.makeEntity(id, 3L, "{\"a\":\"b\"}", Instant.now());
        when(recordRepository.findRecordsAt(eq(id), eq(at), eq(PageRequest.of(0, 1))))
                .thenReturn(List.of(entity));

        RecordEntity result = recordService.getRecordAt(id, at);

        assertSame(entity, result);
        verify(recordRepository).findRecordsAt(eq(id), eq(at), eq(PageRequest.of(0, 1)));
        verifyNoMoreInteractions(recordRepository);
        verifyNoInteractions(latestVersionRepository, objectMapper, jsonMergePatchUtil);
    }

    @Test
    void getRecordAt_shouldThrow_whenNoMatch() {
        long id = 1L;
        Instant at = Instant.parse("2025-01-12T10:15:30Z");

        when(recordRepository.findRecordsAt(eq(id), eq(at), eq(PageRequest.of(0, 1))))
                .thenReturn(List.of());

        assertThrows(NoSuchRecordException.class, () -> recordService.getRecordAt(id, at));

        verify(recordRepository).findRecordsAt(eq(id), eq(at), eq(PageRequest.of(0, 1)));
        verifyNoMoreInteractions(recordRepository);
        verifyNoInteractions(latestVersionRepository, objectMapper, jsonMergePatchUtil);
    }

    // -------------------- upsertRecord --------------------

    @Test
    void upsertRecord_shouldCreateNewRecord_whenNoLatest() throws JsonProcessingException {
        long id = 99L;

        JsonNode patch = realMapper.readTree("{\"foo\":\"bar\"}");
        ObjectNode emptyBase = realMapper.createObjectNode();
        JsonNode merged = realMapper.readTree("{\"foo\":\"bar\"}");

        when(latestVersionRepository.findByIdForUpdate(id)).thenReturn(Optional.empty());
        when(objectMapper.createObjectNode()).thenReturn(emptyBase);
        when(jsonMergePatchUtil.applyMergePatch(emptyBase, patch)).thenReturn(merged);

        // Capture saved record entity and latest version entity
        ArgumentCaptor<RecordEntity> recordCaptor = ArgumentCaptor.forClass(RecordEntity.class);
        ArgumentCaptor<LatestVersionEntity> latestCaptor = ArgumentCaptor.forClass(LatestVersionEntity.class);

        // Simulate repo returning what it saved
        when(recordRepository.save(any(RecordEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        when(latestVersionRepository.save(any(LatestVersionEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        RecordEntity result = recordService.upsertRecord(id, patch);

        // Verify record saved with version 1 and merged data
        verify(recordRepository).save(recordCaptor.capture());
        RecordEntity saved = recordCaptor.getValue();

        assertNotNull(saved.getRecordId());
        assertEquals(id, saved.getRecordId().getId());
        assertEquals(1L, saved.getRecordId().getVersion());
        assertEquals(merged, saved.getData());

        // Verify latest version saved with version 1
        verify(latestVersionRepository).save(latestCaptor.capture());
        LatestVersionEntity latestSaved = latestCaptor.getValue();
        assertEquals(id, latestSaved.getId());
        assertEquals(1L, latestSaved.getVersion());

        // Returned entity should be whatever repository returned
        assertEquals(saved.getRecordId().getVersion(), result.getRecordId().getVersion());
        assertEquals(saved.getData(), result.getData());

        verify(latestVersionRepository).findByIdForUpdate(id);
        verify(objectMapper).createObjectNode();
        verify(jsonMergePatchUtil).applyMergePatch(emptyBase, patch);

        verifyNoMoreInteractions(recordRepository, latestVersionRepository, objectMapper, jsonMergePatchUtil);
    }

    @Test
    void upsertRecord_shouldCreateNextVersion_whenLatestExists() throws Exception {
        long id = 7L;

        LatestVersionEntity latest = LatestVersionEntity.builder().id(id).version(3L).build();

        JsonNode base = realMapper.readTree("{\"a\":1}");
        JsonNode patch = realMapper.readTree("{\"b\":2}");
        JsonNode merged = realMapper.readTree("{\"a\":1,\"b\":2}");

        RecordEntity baseEntity = recordUtil.makeEntity(id, 3L, "{\"a\":1}", Instant.now());

        when(latestVersionRepository.findByIdForUpdate(id)).thenReturn(Optional.of(latest));
        // upsert calls getRecord(id, latest.getVersion()) -> which calls recordRepository.findById(...)
        when(recordRepository.findById(RecordId.builder().id(id).version(3L).build()))
                .thenReturn(Optional.of(baseEntity));

        when(jsonMergePatchUtil.applyMergePatch(base, patch)).thenReturn(merged);

        ArgumentCaptor<RecordEntity> recordCaptor = ArgumentCaptor.forClass(RecordEntity.class);
        ArgumentCaptor<LatestVersionEntity> latestCaptor = ArgumentCaptor.forClass(LatestVersionEntity.class);

        when(recordRepository.save(any(RecordEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        when(latestVersionRepository.save(any(LatestVersionEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        RecordEntity result = recordService.upsertRecord(id, patch);

        verify(recordRepository).save(recordCaptor.capture());
        RecordEntity saved = recordCaptor.getValue();

        assertEquals(id, saved.getRecordId().getId());
        assertEquals(4L, saved.getRecordId().getVersion()); // incremented
        assertEquals(merged, saved.getData());

        verify(latestVersionRepository).save(latestCaptor.capture());
        LatestVersionEntity latestSaved = latestCaptor.getValue();
        assertEquals(id, latestSaved.getId());
        assertEquals(4L, latestSaved.getVersion()); // updated

        assertEquals(4L, result.getRecordId().getVersion());
        assertEquals(merged, result.getData());

        verify(latestVersionRepository).findByIdForUpdate(id);
        verify(recordRepository).findById(RecordId.builder().id(id).version(3L).build());
        verify(jsonMergePatchUtil).applyMergePatch(base, patch);

        // objectMapper.createObjectNode() should NOT be called on existing record path
        verify(objectMapper, never()).createObjectNode();

        verifyNoMoreInteractions(recordRepository, latestVersionRepository, objectMapper, jsonMergePatchUtil);
    }

    @Test
    void upsertRecord_shouldThrow_whenLatestExistsButBaseRecordMissing() throws Exception {
        long id = 7L;

        LatestVersionEntity latest = LatestVersionEntity.builder().id(id).version(3L).build();
        JsonNode patch = realMapper.readTree("{\"b\":2}");

        when(latestVersionRepository.findByIdForUpdate(id)).thenReturn(Optional.of(latest));
        when(recordRepository.findById(RecordId.builder().id(id).version(3L).build()))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchRecordException.class, () -> recordService.upsertRecord(id, patch));

        verify(latestVersionRepository).findByIdForUpdate(id);
        verify(recordRepository).findById(RecordId.builder().id(id).version(3L).build());

        // merge + save should never happen
        verifyNoInteractions(jsonMergePatchUtil);
        verify(recordRepository, never()).save(any());
        verify(latestVersionRepository, never()).save(any());

        verifyNoMoreInteractions(recordRepository, latestVersionRepository);
        verifyNoInteractions(objectMapper);
    }
}
