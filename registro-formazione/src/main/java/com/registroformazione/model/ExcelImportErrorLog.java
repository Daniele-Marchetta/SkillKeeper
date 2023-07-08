package com.registroformazione.model;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="excel_import_error_logs")
//serve per configurare in automatico la data creazione e modifica ad ogni persist
//guardo configurazione springapp bean DateTimeProvider e enablejpaauditing
@EntityListeners(AuditingEntityListener.class)
public class ExcelImportErrorLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String stato;
    
    private String file;
    
    @CreatedDate
    @Column(name = "data_create",updatable = false , nullable = false)
    private OffsetDateTime dataCreazione;
    
    @LastModifiedDate
    @Column(name = "data_update" ,nullable = false)    
    private OffsetDateTime ultimaModifica;

}
