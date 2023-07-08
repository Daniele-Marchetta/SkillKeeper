package com.registroformazione.dto;

import java.time.LocalDate;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistroDto {
    @Size(max = 20, message = "Il tipo deve essere lungo al massimo 20 caratteri")
    @NotNull(message = "Il tipo non può essere null")
    @NotBlank(message = "Il tipo non può essere vuoto")
    @Nullable
    private String tipo;
    private String nota;
    @NotNull(message = "L'anno non può essere null")
    private Integer anno;
    private LocalDate dataCompletamento;
    private LocalDate dataScadenza;
    @NotNull(message = "L'id di persona non può essere null")
    private Integer personaId;
    @NotNull(message = "L'id di attività non può essere null")
    private Integer attivitaId;
    @NotNull(message = "L'id di stato non può essere null")
    private Integer statoId;
}
