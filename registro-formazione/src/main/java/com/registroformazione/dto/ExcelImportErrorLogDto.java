package com.registroformazione.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExcelImportErrorLogDto {

    private String stato;
    private String file;
}
