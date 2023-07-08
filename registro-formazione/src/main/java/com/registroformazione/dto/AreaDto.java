package com.registroformazione.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AreaDto {
	@Size(min = 3,max = 3,message = "il nome dell'area di appartenza deve essere di 3 caratteri")
    @NotNull(message = "il nome non può essere null")
	@NotBlank(message = "Il campo nome non può essere vuoto")
	private String nome;
}
