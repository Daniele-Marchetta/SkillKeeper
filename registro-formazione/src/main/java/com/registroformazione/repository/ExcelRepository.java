package com.registroformazione.repository;

import java.util.List;

import com.registroformazione.model.ExcelImportErrorLog;

public interface ExcelRepository {
    int saveAree();
    int saveStati();
    int saveCc();
    int saveVendors();
    int saveCompetenze();
    int savePersone();
    int saveAttivita();
    int saveRegistro();
    Integer checkState(String stato);
}
