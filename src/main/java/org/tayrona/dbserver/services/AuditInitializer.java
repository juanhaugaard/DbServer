package org.tayrona.dbserver.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.tayrona.dbserver.audit.InitializeAudit;

import javax.annotation.PostConstruct;

@Slf4j
@Component
@DependsOn("embeddedServer")
public class AuditInitializer {
    private static final String CLASS_NAME = AuditInitializer.class.getSimpleName();

    @PostConstruct
    public void setup() {
        log.debug("{}.setup()", CLASS_NAME);
        int statementCount = InitializeAudit.execute();
        log.debug("{}.setUp() executed {} statements", CLASS_NAME, statementCount);
    }
}
