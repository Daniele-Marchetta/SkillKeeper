package com.registroformazione.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelValidationErrorsDto {

    private String columnName;
    private Integer rowCount;
    private String errorType;

}
