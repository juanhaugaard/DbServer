package org.tayrona.dbserver.audit;

import lombok.extern.slf4j.Slf4j;
import org.tayrona.dbserver.BaseFunction;

import static org.tayrona.dbserver.audit.LogFunction.initAudit;

@Slf4j
public class InitializeAudit extends BaseFunction {
    private static final String CLASS_NAME = InitializeAudit.class.getSimpleName();

    public static int execute() {
        log.debug("{}.execute()", CLASS_NAME);
        int count = 0;
        for (String sql : initAudit()) {
            log.debug("{}.execute() - {}", CLASS_NAME, sql);
            executeSqlStatement(sql);
            count += 1;
        }
        return count;
    }
}
