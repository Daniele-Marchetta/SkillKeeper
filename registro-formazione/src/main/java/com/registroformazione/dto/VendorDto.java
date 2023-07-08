package com.registroformazione.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VendorDto {
	@Size(max=20, message="Il nome deve essere lungo al massimo 20 caratteri")
	@NotNull(message="Il vendor non può essere null")
	@NotBlank(message="Il vendor non può essere vuoto")
	private String nome;
}
