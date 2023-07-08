package com.registroformazione.model;

import java.time.LocalDate;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SQLDelete(sql = "UPDATE Registro SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
public class Registro {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String tipo;
	private String nota;
	private Integer anno; 
	@Column(name="data_completamento")
	private LocalDate dataCompletamento;
	@Column(name="data_scadenza")
	private LocalDate dataScadenza;
	@ManyToOne()
    @JoinColumn(name = "persona_id")
	@JsonManagedReference
    private Persona persona;
	@ManyToOne()
    @JoinColumn(name = "attivita_id")
	@JsonManagedReference
    private Attivita attivita;
	@ManyToOne()
    @JoinColumn(name = "stato_id")
	@JsonManagedReference
    private Stato stato;
	@JsonIgnore
    private boolean deleted = Boolean.FALSE;
	

}
