package org.tayrona.dbserver.services.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

@Data
public class Row {
//    @JsonAlias("ID")
//    private BigInteger ID;
//    @JsonAlias("AN_ID")
//    private Integer AN_ID;
//    @JsonAlias("S_TIME")
//    private String S_TIME;
//    @JsonAlias("A_TIME")
//    private Time A_TIME;
//    @JsonAlias("A_DATE")
//    private Date A_DATE;
//    @JsonAlias("DATE_TIME")
//    private Timestamp DATE_TIME;
//    @JsonAlias("A_DECIMAL")
//    private BigDecimal A_DECIMAL;
//    @JsonAlias("A_DOUBLE")
//    private Double A_DOUBLE;
//    @JsonAlias("A_REAL")
//    private Float A_REAL;
//    @JsonAlias("A_BIGINT")
//    private BigInteger A_BIGINT;
    private BigInteger id;
    private Integer anId;
    @JsonAlias("STime")
    private String stime;
    @JsonAlias("ATime")
    private Time atime;
    @JsonAlias("ADate")
    private Date adate;
    private Timestamp dateTime;
    @JsonAlias("ADecimal")
    private BigDecimal adecimal;
    @JsonAlias("ADouble")
    private Double adouble;
    @JsonAlias("AReal")
    private Float areal;
    @JsonAlias("ABigint")
    private BigInteger abigint;
}
