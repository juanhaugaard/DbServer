package org.tayrona.dbserver.audit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EnableLog extends LogFunction {
    private static final String CLASS_NAME = EnableLog.class.getSimpleName();

    public static int execute(String tableName) {
        log.debug("{}.execute({})", CLASS_NAME, tableName);
        int count=0;
        for (String fmt : triggersCreate()) {
            executeSqlStatement(fmt, tableName);
            count += 1;
        }
        return count;
    }
}
