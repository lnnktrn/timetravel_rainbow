package com.lnnktrn.timetravel_rainbow.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.lnnktrn.timetravel_rainbow.json.JsonNodeConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "records")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RecordEntity {
    @EmbeddedId
    private RecordId recordId;
    @Convert(converter = JsonNodeConverter.class)
    @Column(name = "data", columnDefinition = "TEXT")
    private JsonNode data;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}