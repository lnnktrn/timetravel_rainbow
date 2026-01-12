package com.lnnktrn.timetravel_rainbow.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "latest_versions")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class LatestVersionEntity {
    @Id
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private Long version;

}
