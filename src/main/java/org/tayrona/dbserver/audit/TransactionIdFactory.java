package org.tayrona.dbserver.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@Component
public class TransactionIdFactory {
    private static final String CLASS_NAME = TransactionIdFactory.class.getSimpleName();
    private static ZoneId zoneId = ZoneId.of("UTC");
    private static long seq = 0;
    private static ZonedDateTime today = ZonedDateTime.now(zoneId);
    private static long numOfDays = today.toLocalDate().toEpochDay();
    private JdbcTemplate jdbcTemplate;

    public TransactionIdFactory() {
        log.debug("{} constructed", CLASS_NAME);
    }

    @PostConstruct
    public void initialize() {
        log.debug("{}.initialize()", CLASS_NAME);
        String sql = "SELECT TDAY, TSEQ FROM AUDIT.EVENTS ORDER BY TDAY DESC, TSEQ DESC LIMIT 1";
        List<TransactionIdentifier> result = jdbcTemplate.query(sql, new TransactionIdentifierRowMapper());
        if ((result != null) && !result.isEmpty()) {
            TransactionIdentifier lastTranId = result.get(0);
            synchronized (this) {
                if (numOfDays <= lastTranId.getNumOfDays()) {
                    numOfDays = lastTranId.getNumOfDays();
                    seq = lastTranId.getSeq() + 1;
                    log.debug("{}.initialize() - continue transaction ID sequence, numOfDays:{}", CLASS_NAME, numOfDays);
                } else {
                    seq = 0L;
                    log.debug("{}.initialize() - rollover transaction ID sequence, numOfDays:{}", CLASS_NAME, numOfDays);
                }
            }
        }
    }

    private Long calcNumOfDays() {
        return ZonedDateTime.now(zoneId).toLocalDate().toEpochDay();
    }

    private synchronized void conditionalRollover() {
        long currentNumberOfDays = calcNumOfDays();
        if (currentNumberOfDays > numOfDays) {
            seq = 0;
            numOfDays = currentNumberOfDays;
            log.debug("{}.conditionalRollover() - rollover transaction ID sequence, numOfDays:{}", CLASS_NAME, numOfDays);
        }
    }

    public synchronized TransactionIdentifier get() {
        conditionalRollover();
        return new TransactionIdentifier(numOfDays, seq++);
    }

    private class TransactionIdentifierRowMapper implements RowMapper<TransactionIdentifier> {
        /**
         * Implementations must implement this method to map each row of data
         * in the ResultSet. This method should not call {@code next()} on
         * the ResultSet; it is only supposed to map values of the current row.
         *
         * @param rs     the ResultSet to map (pre-initialized for the current row)
         * @param rowNum the number of the current row
         * @return the result object for the current row (may be {@code null})
         * @throws SQLException if an SQLException is encountered getting
         *                      column values (that is, there's no need to catch SQLException)
         */
        @Override
        public TransactionIdentifier mapRow(ResultSet rs, int rowNum) throws SQLException {
            TransactionIdentifier ret = new TransactionIdentifier();
            ret.setNumOfDays(rs.getLong("TDAY"));
            ret.setSeq(rs.getLong("TSEQ"));
            return ret;
        }
    }

    @Autowired
    @Qualifier("JdbcTemplate")
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
