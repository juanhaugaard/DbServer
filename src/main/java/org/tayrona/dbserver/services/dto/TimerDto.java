package org.tayrona.dbserver.services.dto;

import lombok.Data;

@Data
public class TimerDto {
    private RowDto oldRow;
    private RowDto newRow;
}
