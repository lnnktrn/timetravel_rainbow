package com.lnnktrn.timetravel_rainbow.controller.v1;

import com.lnnktrn.timetravel_rainbow.entity.RecordEntity;
import com.lnnktrn.timetravel_rainbow.repository.RecordRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecordController.class)
class RecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecordRepository repo;

    //----------------------------POST----------------------------
    @Test
    void post_createsNewRecord_whenRecordDoesNotExist() throws Exception {
        when(repo.findById(1L)).thenReturn(Optional.empty());
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(post("/api/v1/records/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"a\":\"1\"}"))
                .andExpect(status().isCreated());
    }

    //----------------------------GET----------------------------
    @Test
    void get_returnsPayload_whenRecordExists() throws Exception {
        RecordEntity entity = RecordEntity.builder()
                .id(1L)
                .data("{\"a\":\"1\"}")
                .build();

        when(repo.findById(1L)).thenReturn(Optional.of(entity));

        mockMvc.perform(get("/api/v1/records/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.data").value("{\"a\":\"1\"}"));
    }

    @Test
    void get_returns404_whenRecordDoesNotExist() throws Exception {
        when(repo.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/records/1"))
                .andExpect(status().isNotFound());
    }
}