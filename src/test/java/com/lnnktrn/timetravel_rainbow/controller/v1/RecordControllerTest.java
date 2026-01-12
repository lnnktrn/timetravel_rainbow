package com.lnnktrn.timetravel_rainbow.controller.v1;

import com.lnnktrn.timetravel_rainbow.entity.RecordEntity;
import com.lnnktrn.timetravel_rainbow.entity.RecordId;
import com.lnnktrn.timetravel_rainbow.exception.NoSuchRecordException;
import com.lnnktrn.timetravel_rainbow.service.RecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(RecordController.class)
class RecordControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    RecordService recordService;

    @Test
    void getRecord_shouldReturn200_andBody() throws Exception {
        long id = 1L;
        long version = 1L;
        String body = "{\"a\":1}";
        Instant createdAt = Instant.parse("2025-01-01T00:00:00Z");

        RecordEntity entity = RecordEntity.builder()
                .recordId(RecordId.builder().id(id).version(version).build())
                .data(body)
                .createdAt(createdAt)
                .build();

        when(recordService.getRecord(id)).thenReturn(entity);

        mockMvc.perform(get("/api/v1/records/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.data").value(body))
                .andExpect(jsonPath("$.version").value(version))
                .andExpect(jsonPath("$.createdAt").value(createdAt.toString()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(recordService).getRecord(id);
        verifyNoMoreInteractions(recordService);
    }

    @Test
    void upsertRecord_shouldReturn201_andCallService() throws Exception {
        long id = 1L;
        String body = "{\"a\":1}";

        mockMvc.perform(post("/api/v1/records/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(content().string("")); // Void body

        verify(recordService).upsertRecord(id, body);
        verifyNoMoreInteractions(recordService);
    }

    @Test
    void getRecord_shouldReturn400_whenIdIsZero() throws Exception {
        mockMvc.perform(get("/api/v1/records/{id}", 0))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(recordService);
    }

    @Test
    void upsertRecord_shouldReturn400_whenIdIsNegative() throws Exception {
        mockMvc.perform(post("/api/v1/records/{id}", -5)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(recordService);
    }

    @Test
    void get_returns404_whenRecordDoesNotExist() throws Exception {
        Long id = 1L;
        when(recordService.getRecord(id)).thenThrow(new NoSuchRecordException(id));
        mockMvc.perform(get("/api/v1/records/{id}", id))
                .andExpect(status().isNotFound());
    }
}
