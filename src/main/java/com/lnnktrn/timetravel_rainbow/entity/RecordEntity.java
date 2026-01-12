package com.lnnktrn.timetravel_rainbow.entity;

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
    private String data;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}