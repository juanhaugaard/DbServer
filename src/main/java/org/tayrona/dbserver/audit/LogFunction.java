package org.tayrona.dbserver.audit;

import org.tayrona.dbserver.BaseFunction;

import java.util.List;

abstract class LogFunction extends BaseFunction {

    static List<String> triggersCreate() {
        return h2Config().getAudit().getTriggerCreate();
    }

    static List<String> triggersDrop() {
        return h2Config().getAudit().getTriggerDrop();
    }

    static List<String> initAudit() {
        return h2Config().getAudit().getInitSql();
    }
}
