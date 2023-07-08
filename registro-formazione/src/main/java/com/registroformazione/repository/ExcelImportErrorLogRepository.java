package com.registroformazione.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.registroformazione.model.ExcelImportErrorLog;

public interface ExcelImportErrorLogRepository extends JpaRepository<ExcelImportErrorLog, Integer> {

}
