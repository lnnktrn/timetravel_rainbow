package com.lnnktrn.timetravel_rainbow.mapper;

import com.lnnktrn.timetravel_rainbow.dto.RecordDto;
import com.lnnktrn.timetravel_rainbow.dto.RecordVersionDto;
import com.lnnktrn.timetravel_rainbow.entity.RecordEntity;

public class EntityToDtoMapper {
    public static RecordDto mapRecordEntityToRecorDto(RecordEntity recordEntity) {
       return new RecordDto(
                recordEntity.getRecordId().getId(),
                recordEntity.getData());
    }

    public static RecordVersionDto mapRecorEntityToRecorVersionDto(RecordEntity recordEntity) {
        return new RecordVersionDto(
                recordEntity.getRecordId().getId(),
                recordEntity.getRecordId().getVersion(),
                recordEntity.getData(),
                recordEntity.getCreatedAt());
    }
}
