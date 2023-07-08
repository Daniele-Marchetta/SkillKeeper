package com.registroformazione.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.registroformazione.dto.AttivitaDto;
import com.registroformazione.model.Attivita;
import com.registroformazione.service.AttivitaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/api")
public class AttivitaController {
    @Autowired
    private AttivitaService serv;

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "get di tutte le attività")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "restituita la lista di attività", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = AttivitaDto.class)) }),
            @ApiResponse(responseCode = "204", description = "La lista è vuota") })
    @GetMapping("/attivita")
    public ResponseEntity<List<AttivitaDto>> findAll() {
        List<AttivitaDto> attivita = serv.findAll();
        log.info("Lista attivita ritornate");
        return new ResponseEntity<>(attivita, HttpStatus.OK);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "get di un' attività")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "restituita singola attività", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = AttivitaDto.class)) }) })
    @GetMapping("/attivita/{id}")
    public ResponseEntity<AttivitaDto> findById(@PathVariable Integer id) {
        AttivitaDto attivita = serv.findById(id);
        log.info("attivita ritornata");
        return new ResponseEntity<>(attivita, HttpStatus.OK);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "inserimento attività")
    @ApiResponses(value = { @ApiResponse(responseCode = "201", description = "attività creata", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = AttivitaDto.class)) }) })
    @PostMapping("/attivita")
    public ResponseEntity<AttivitaDto> create(@RequestBody @Valid AttivitaDto a) {
        AttivitaDto att = serv.create(a);
        log.info("Attività creata correttamente");
        log.debug("Attività creata correttamente: " + att.toString());
        return new ResponseEntity<>(att, HttpStatus.CREATED);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "update attività")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "attività aggiornata", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = AttivitaDto.class)) }) })
    @PatchMapping("/attivita/{id}")
    public ResponseEntity<AttivitaDto> update(@PathVariable Integer id, @RequestBody @Valid AttivitaDto a) {
        AttivitaDto att = serv.update(id, a);
        log.info("Attività aggiornata correttamente");
        log.debug("Attività aggiornata correttamente: " + att.toString());
        return new ResponseEntity<>(att, HttpStatus.OK);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "cancellazione attività")
    @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Attività cancellata") })
    @DeleteMapping("/attivita/{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id) {
        serv.delete(id);
        log.info("Attività cancellata correttamente.");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
