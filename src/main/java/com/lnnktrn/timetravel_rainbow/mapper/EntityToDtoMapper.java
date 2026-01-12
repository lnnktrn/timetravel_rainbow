package com.lnnktrn.timetravel_rainbow.mapper;

import com.lnnktrn.timetravel_rainbow.dto.RecordDto;
import com.lnnktrn.timetravel_rainbow.entity.RecordEntity;

public class EntityToDtoMapper {
    public static RecordDto map(RecordEntity recordEntity) {
       return new RecordDto(
                recordEntity.getRecordId().getId(),
                recordEntity.getRecordId().getVersion(),
                recordEntity.getData().toString(),
                recordEntity.getCreatedAt());
    }
}
