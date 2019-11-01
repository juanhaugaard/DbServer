package org.tayrona.dbserver.audit;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class TransactionIdFactory {
    private static long seq;
    private static Date today;
    private JdbcTemplate jdbcTemplate;

    public TransactionIdFactory() {
        rollover();
    }

    @PostConstruct
    public void initialize() {
        String sql = "SELECT TDATE, TSEQ FROM AUDIT.EVENTS ORDER BY TDATE DESC, TSEQ DESC LIMIT 1";
        List<TranId> result = jdbcTemplate.query(sql, new TranIdRowMapper());
        if ((result != null) && !result.isEmpty()) {
            TranId lastTranId = result.get(0);
            synchronized (this) {
                if (!today.after(lastTranId.getDate())) {
                    today = lastTranId.getDate();
                    seq = lastTranId.getSeq() + 1;
                }
            }
        }
    }

    private synchronized boolean checkForRollover() {
        Date current = new Date(System.currentTimeMillis());
        return ((today == null) || current.after(today));
    }

    private synchronized void rollover() {
        seq = 0;
        today = new Date(System.currentTimeMillis());
    }

    public synchronized TransactionIdentifier get() {
        if (checkForRollover()) {
            rollover();
        }
        return new TransactionIdentifier(today, seq++);
    }

    @Data
    private class TranId {
        private Date date;
        private long seq;
    }

    private class TranIdRowMapper implements RowMapper<TranId> {
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
        public TranId mapRow(ResultSet rs, int rowNum) throws SQLException {
            TranId ret = new TranId();
            ret.setDate(rs.getDate("TDATE"));
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
