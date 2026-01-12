package com.lnnktrn.timetravel_rainbow.repository;

import com.lnnktrn.timetravel_rainbow.entity.RecordEntity;
import com.lnnktrn.timetravel_rainbow.entity.RecordId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecordRepository extends JpaRepository<RecordEntity, RecordId> {
    List<RecordEntity> findAllByRecordId_IdOrderByRecordId_VersionAsc(Long id);

}