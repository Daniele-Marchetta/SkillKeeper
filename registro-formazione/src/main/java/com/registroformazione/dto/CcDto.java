package com.registroformazione.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CcDto {
	@Size(max = 20 , message = "il campo nome deve essere minore di 20 caratteri")
	@NotNull(message = "il campo nome non può essere null")
	@NotBlank(message="il campo nome non può essere vuoto")
	private String nome;
}
