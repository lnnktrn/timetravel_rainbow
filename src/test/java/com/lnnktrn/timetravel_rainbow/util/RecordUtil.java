package com.lnnktrn.timetravel_rainbow.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lnnktrn.timetravel_rainbow.entity.RecordEntity;
import com.lnnktrn.timetravel_rainbow.entity.RecordId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class RecordUtil {

    @Autowired
    private ObjectMapper objectMapper;

    public RecordEntity makeEntity(long id, long version, String jsonData, Instant createdAt) {
        var node = makeJsonNode(jsonData);
        RecordId recordId = RecordId.builder().id(id).version(version).build(); // предполагается, что у тебя есть такой конструктор
        return RecordEntity.builder()
                .recordId(recordId)
                .data(node)
                .createdAt(createdAt)
                .build();
    }

    public ObjectNode makeJsonNode (String jsonData) {
        ObjectNode node = objectMapper.createObjectNode();
        try {
            node = (ObjectNode) objectMapper.readTree(jsonData);
        } catch (Exception e) {
            // fallback
            node.put("raw", jsonData);
        }
        return node;
    }



}
