package org.tayrona.dbserver.services.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import org.tayrona.dbserver.services.dto.RowDto;
import org.tayrona.dbserver.services.model.Row;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Mapper(componentModel = "spring")
public interface RowMapper {
    RowMapper INSTANCE = Mappers.getMapper(RowMapper.class);

    default Time map(LocalTime value) {
        return (value == null) ? null : Time.valueOf(value);
    }

    default LocalTime map(Time value) {
        return (value == null) ? null : value.toLocalTime();
    }

    default Timestamp map(ZonedDateTime value) {
        return (value == null) ? null : Timestamp.from(value.toInstant());
    }

    default ZonedDateTime map(Timestamp value) {
        return (value == null) ? null : ZonedDateTime.ofInstant(value.toInstant(), ZoneId.systemDefault());
    }

    default Date map(LocalDate value) {
        return (value == null) ? null : Date.valueOf(value);
    }

    default LocalDate map(Date value) {
        return (value == null) ? null : value.toLocalDate();
    }

//    @Mappings({
//            @Mapping(target = "ID", source = "id"),
//            @Mapping(target = "AN_ID", source = "anId"),
//            @Mapping(target = "s_TIME", source = "STime"),
//            @Mapping(target = "a_TIME", source = "ATime"),
//            @Mapping(target = "a_DATE", source = "ADate"),
//            @Mapping(target = "DATE_TIME", source = "dateTime"),
//            @Mapping(target = "a_DECIMAL", source = "ADecimal"),
//            @Mapping(target = "a_DOUBLE", source = "ADouble"),
//            @Mapping(target = "a_REAL", source = "AReal"),
//            @Mapping(target = "a_BIGINT", source = "ABigint")})
    @Mappings({
            @Mapping(target = "stime", source = "STime"),
            @Mapping(target = "atime", source = "ATime"),
            @Mapping(target = "adate", source = "ADate"),
            @Mapping(target = "adecimal", source = "ADecimal"),
            @Mapping(target = "adouble", source = "ADouble"),
            @Mapping(target = "areal", source = "AReal"),
            @Mapping(target = "abigint", source = "ABigint")})
    Row toModel(RowDto rowDto);

//    @Mappings({
//            @Mapping(target = "id", source = "ID"),
//            @Mapping(target = "anId", source = "AN_ID"),
//            @Mapping(target = "STime", source = "s_TIME"),
//            @Mapping(target = "ATime", source = "a_TIME"),
//            @Mapping(target = "ADate", source = "a_DATE"),
//            @Mapping(target = "dateTime", source = "DATE_TIME"),
//            @Mapping(target = "ADecimal", source = "a_DECIMAL"),
//            @Mapping(target = "ADouble", source = "a_DOUBLE"),
//            @Mapping(target = "AReal", source = "a_REAL"),
//            @Mapping(target = "ABigint", source = "a_BIGINT")})
    @Mappings({
            @Mapping(target = "STime", source = "stime"),
            @Mapping(target = "ATime", source = "atime"),
            @Mapping(target = "ADate", source = "adate"),
            @Mapping(target = "ADecimal", source = "adecimal"),
            @Mapping(target = "ADouble", source = "adouble"),
            @Mapping(target = "AReal", source = "areal"),
            @Mapping(target = "ABigint", source = "abigint")})
    RowDto toDto(Row row);
}
