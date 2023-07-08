package com.registroformazione.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.registroformazione.model.ExcelValidationErrors;

public interface ExcelValidationErrorsRepository extends JpaRepository<ExcelValidationErrors, Integer> {



}
