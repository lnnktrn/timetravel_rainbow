package com.lnnktrn.timetravel_rainbow.repository;

import com.lnnktrn.timetravel_rainbow.entity.RecordEntity;
import com.lnnktrn.timetravel_rainbow.entity.RecordId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface RecordRepository extends JpaRepository<RecordEntity, RecordId> {
    List<RecordEntity> findAllByRecordId_IdOrderByRecordId_VersionAsc(Long id);

    @Query("""
                SELECT r
                FROM RecordEntity r
                WHERE r.recordId.id = :id
                  AND r.createdAt <= :at
                ORDER BY r.createdAt DESC
            """)
    List<RecordEntity> findRecordsAt(
            @Param("id") Long id,
            @Param("at") Instant at,
            Pageable pageable
    );
}