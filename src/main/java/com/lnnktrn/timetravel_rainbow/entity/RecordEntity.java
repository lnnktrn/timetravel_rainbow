package com.lnnktrn.timetravel_rainbow.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "records")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RecordEntity {

    @Id
    Long id;

    String data;
}