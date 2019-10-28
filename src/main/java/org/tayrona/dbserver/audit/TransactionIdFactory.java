package org.tayrona.dbserver.audit;

import org.springframework.stereotype.Component;

import java.sql.Date;

@Component
public class TransactionIdFactory {
    private static long seq = 0;
    public TransactionIdentifier get() {
        return new TransactionIdentifier(new Date(System.currentTimeMillis()), seq++);
    }
}
