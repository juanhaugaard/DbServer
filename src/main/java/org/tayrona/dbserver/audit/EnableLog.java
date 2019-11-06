package org.tayrona.dbserver.audit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EnableLog implements LogFunction {
    private static final String CLASS_NAME = EnableLog.class.getSimpleName();

    public static int execute(String tablename) {
        log.debug("{}.execute({})", CLASS_NAME, tablename);
        int count=0;
        for (String fmt : triggersCreate) {
            LogFunction.executeSqlStatement(fmt, tablename);
            count += 1;
        }
        return count;
    }
}
