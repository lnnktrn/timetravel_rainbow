package com.lnnktrn.timetravel_rainbow.repository;

import com.lnnktrn.timetravel_rainbow.entity.RecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends JpaRepository<RecordEntity, Long> {
}