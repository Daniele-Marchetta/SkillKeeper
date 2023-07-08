package com.registroformazione.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AttivitaDto {
	@Size(max = 50,message = "il nome dell'area di appartenza deve essere minore di 50 caratteri")
    @NotNull(message = "il campo nome  non può essere null")
	@NotBlank(message = "inserisci il nome dell'attività")
	private String nome;
	@Size(max = 10,message = "il campo codice deve essere minore di 10 caratteri")
	@NotBlank(message = "inserisci il codice dell'attività")
	private String codice;
	private Integer competenzaId;
	private Integer vendorId;
}
