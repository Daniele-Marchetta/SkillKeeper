package com.registroformazione.model;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringExclude;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Attivita {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String nome;
	private String codice;

	@ManyToOne()
    @JoinColumn(name = "competenza_id")
	@JsonManagedReference
    private Competenza competenza;	
	@ManyToOne()
    @JoinColumn(name = "vendor_id")
	@JsonManagedReference
    private Vendor vendor;
	@OneToMany(mappedBy = "attivita", fetch = FetchType.EAGER)
	@ToStringExclude
	@JsonBackReference
	private List<Registro> registro;
}
