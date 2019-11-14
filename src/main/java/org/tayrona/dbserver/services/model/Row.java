package org.tayrona.dbserver.services.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class Row {
//    private BigInteger ID;
//    private Integer AN_ID;
//    private String S_TIME;
//    private LocalTime A_TIME;
//    private LocalDate A_DATE;
//    private LocalDateTime DATE_TIME;
//    private BigDecimal A_DECIMAL;
//    private Double A_DOUBLE;
//    private Float A_REAL;
//    private BigInteger A_BIGINT;
    @JsonProperty(required = true)
    @JsonAlias("ID")
    private BigInteger id;
    @JsonProperty(required = true)
    @JsonAlias("AN_ID")
    private Integer anId;
    @JsonProperty(required = true)
    @JsonAlias("S_TIME")
    private String sTime;
    @JsonProperty(required = true)
    @JsonAlias("A_TIME")
    private LocalTime aTime;
    @JsonProperty(required = true)
    @JsonAlias("A_DATE")
    private LocalDate aDate;
    @JsonProperty(required = true)
    @JsonAlias("DATE_TIME")
    private Timestamp dateTime;
    @JsonProperty(required = true)
    @JsonAlias("A_DECIMAL")
    private BigDecimal aDecimal;
    @JsonProperty(required = true)
    @JsonAlias("A_DOUBLE")
    private Double aDouble;
    @JsonProperty(required = true)
    @JsonAlias("A_REAL")
    private Float aReal;
    @JsonProperty(required = true)
    @JsonAlias("A_BIGINT")
    private BigInteger aBigint;
}
