package org.tayrona.dbserver.services.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.tayrona.dbserver.services.dto.RowDto;
import org.tayrona.dbserver.services.dto.TimerDto;
import org.tayrona.dbserver.services.model.Row;
import org.tayrona.dbserver.services.model.Timer;

@Mapper(componentModel = "spring")
public interface TimerMapper {
    TimerMapper INSTANCE = Mappers.getMapper(TimerMapper.class);

    default Row map(RowDto value) {
        return (value == null) ? null : RowMapper.INSTANCE.toModel(value);
    }

    default RowDto map(Row value) {
        return (value == null) ? null : RowMapper.INSTANCE.toDto(value);
    }

    Timer toModel(TimerDto timerDto);

    TimerDto toDto(Timer timer);
}
