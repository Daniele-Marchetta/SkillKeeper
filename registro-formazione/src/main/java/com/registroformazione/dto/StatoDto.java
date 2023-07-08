package com.registroformazione.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StatoDto {
	@Size(max=20, message="Il nome deve essere lungo al massimo 20 caratteri")
	@NotNull(message="Lo stato non può essere null")
	@NotBlank(message="Lo stato non può essere vuoto")
	private String nome;
}
