package org.tayrona.dbserver;

import com.fasterxml.jackson.databind.util.StdDateFormat;

public interface Constants {
    String DEFAULT_DATE_FORMAT = StdDateFormat.DATE_FORMAT_STR_ISO8601;
    String RESOURCE_PREFIX = "classpath:";
}
