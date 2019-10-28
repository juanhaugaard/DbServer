package org.tayrona.dbserver.audit;

import lombok.Data;

import java.sql.Date;

@Data
public class TransactionIdentifier {
    private Date date;
    private Long seq;

    public TransactionIdentifier(Date date, Long seq) {
        this.date = date;
        this.seq = seq;
    }
}
