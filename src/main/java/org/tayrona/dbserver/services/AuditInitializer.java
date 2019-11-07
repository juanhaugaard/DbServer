package org.tayrona.dbserver.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.tayrona.dbserver.audit.InitializeAudit;

import javax.annotation.PostConstruct;
import java.sql.SQLException;

@Slf4j
@Component
public class AuditInitializer {
    private static final String CLASS_NAME = AuditInitializer.class.getSimpleName();

    @PostConstruct
    public void setup() throws SQLException {
        log.debug("{}.setup()", CLASS_NAME);
        InitializeAudit.execute();
    }
}
