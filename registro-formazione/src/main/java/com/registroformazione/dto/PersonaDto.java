package com.registroformazione.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonaDto {
	@Size(max=20, message="Il nome deve essere lungo al massimo 20 caratteri")
	@NotNull(message="Il nome non può essere null")
	@NotBlank(message="Il nome non può essere vuoto")
	private String nome;
	@Size(max=20, message="Il cognome deve essere lungo al massimo 20 caratteri")
	@NotNull(message="Il cognome non può essere null")
	@NotBlank(message="Il cognome non può essere vuoto")
	private String cognome;
    private boolean inForza;
    @NotNull(message="L'id di cc non può essere null")
    private Integer ccId;
    @NotNull(message="L'id di area non può essere null")
    private Integer areaId;


}
