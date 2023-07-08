package com.registroformazione.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CompetenzaDto {
	@Size(max = 50 , message = "il campo nome deve essere massimo di 50 caratteri")
	@NotNull(message = "il campo nome non può essere null")
	@NotBlank(message="il campo nome non può essere vuoto")
	private String nome;
}
