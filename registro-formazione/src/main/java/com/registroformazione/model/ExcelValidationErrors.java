package com.registroformazione.model;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "errors_in_staging")
@Immutable
public class ExcelValidationErrors {
    @Id
    private Integer id;
    @Column(name = "column_name")
    private String columnName;
    @Column(name = "row_count")
    private Integer rowCount;
    @Column(name = "error_type")
    private String errorType;
}
