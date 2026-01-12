package com.lnnktrn.timetravel_rainbow.controller.v2;

import com.lnnktrn.timetravel_rainbow.entity.RecordEntity;
import com.lnnktrn.timetravel_rainbow.entity.RecordId;
import com.lnnktrn.timetravel_rainbow.service.RecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecordVersionController.class)
class RecordVersionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecordService recordService;

    @Test
    void getLatestOrByVersion_shouldCallGetLatest_whenVersionMissing() throws Exception {
        long id = 1L;
        long version = 1L;
        String body = "{\"a\":1}";
        Instant createdAt = Instant.parse("2025-01-01T00:00:00Z");

        RecordEntity entity = RecordEntity.builder()
                .recordId(RecordId.builder().id(id).version(version).build())
                .data(body)
                .createdAt(createdAt)
                .build();

        when(recordService.getLatestRecord(id)).thenReturn(entity);

        mockMvc.perform(get("/api/v2/records/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.data").value(body))
                .andExpect(jsonPath("$.version").value(version))
                .andExpect(jsonPath("$.createdAt").value(createdAt.toString()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(recordService).getLatestRecord(id);
        verify(recordService, never()).getRecord(anyLong(), anyLong());
        verifyNoMoreInteractions(recordService);
    }

    @Test
    void getLatestOrByVersion_shouldCallGetRecord_whenVersionProvided() throws Exception {
        long id = 1L;
        long version = 2L;
        String body = "{\"a\":1}";
        Instant createdAt = Instant.parse("2025-01-01T00:00:00Z");

        RecordEntity entity = RecordEntity.builder()
                .recordId(RecordId.builder().id(id).version(version).build())
                .data(body)
                .createdAt(createdAt)
                .build();

        when(recordService.getRecord(id, version)).thenReturn(entity);

        mockMvc.perform(get("/api/v2/records/{id}", id)
                        .param("version", String.valueOf(version)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.data").value(body))
                .andExpect(jsonPath("$.version").value(version))
                .andExpect(jsonPath("$.createdAt").value(createdAt.toString()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(recordService).getRecord(id, version);
        verify(recordService, never()).getLatestRecord(anyLong());
        verifyNoMoreInteractions(recordService);
    }

    @Test
    void getLatestOrByVersion_shouldReturn400_whenIdIsZero_ifMethodValidationEnabled() throws Exception {
        mockMvc.perform(get("/api/v2/records/{id}", 0L))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(recordService);
    }

    @Test
    void getLatestOrByVersion_shouldReturn400_whenVersionIsZero_ifMethodValidationEnabled() throws Exception {
        mockMvc.perform(get("/api/v2/records/{id}", 1L)
                        .param("version", "0"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(recordService);
    }

    @Test
    void listVersions_shouldReturn200_andJsonArray() throws Exception {
        long id = 1L;

        RecordEntity e1 = RecordEntity.builder().recordId(
                        RecordId.builder().id(id).build())
                .data("{}")
                .build();
        RecordEntity e2 = RecordEntity.builder().recordId(
                        RecordId.builder().id(id).build())
                .data("{}")
                .build();

        when(recordService.listVersions(id)).thenReturn(List.of(e1, e2));

        mockMvc.perform(get("/api/v2/records/{id}/history", id))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(recordService).listVersions(id);
        verifyNoMoreInteractions(recordService);
    }

    @Test
    void listVersions_shouldReturn200_andEmptyArray_whenNoVersions() throws Exception {
        long id = 1L;

        when(recordService.listVersions(id)).thenReturn(List.of());

        mockMvc.perform(get("/api/v2/records/{id}/history", id))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(recordService).listVersions(id);
        verifyNoMoreInteractions(recordService);
    }

    @Test
    void listVersions_shouldReturn400_whenIdIsZero_ifMethodValidationEnabled() throws Exception {
        mockMvc.perform(get("/api/v2/records/{id}/history", 0L))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(recordService);
    }
}
