package org.tayrona.dbserver.audit;

import lombok.Data;

@Data
public class TransactionIdentifier {
    private Long numOfDays;
    private Long seq;

    public TransactionIdentifier() { }

    public TransactionIdentifier(Long numOfDays, Long seq) {
        this.numOfDays = numOfDays;
        this.seq = seq;
    }
}
