package org.tayrona.dbserver.services.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;

@Data
public class RowDto {
    private BigInteger id;
    private Integer anId;
    private String STime;
    private LocalTime ATime;
    private LocalDate ADate;
    private ZonedDateTime dateTime;
    private BigDecimal ADecimal;
    private Double ADouble;
    private Float AReal;
    private BigInteger ABigint;
}
