package org.tayrona.dbserver.audit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InitializeAudit extends LogFunction {
    private static final String CLASS_NAME = InitializeAudit.class.getSimpleName();

    public static int execute() {
        log.debug("{}.execute()", CLASS_NAME);
        int count = 0;
        for (String sql : initAudit()) {
            log.debug("{}.execute() - executing: {}", CLASS_NAME, sql);
            executeSqlStatement(sql);
            count += 1;
        }
        return count;
    }
}
