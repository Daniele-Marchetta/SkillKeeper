package com.registroformazione.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.registroformazione.dto.RegistroDto;
import com.registroformazione.model.ExcelImportErrorLog;
import com.registroformazione.model.ExcelValidationErrors;
import com.registroformazione.service.RegistroService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/api")
@Log4j2
public class RegistroController {

    @Autowired
    private RegistroService serv;

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get di tutti i registri")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restituito il registro", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = RegistroDto.class)) }),
            @ApiResponse(responseCode = "204", description = "La lista è vuota") })
    @GetMapping("/registro/{offset}/{page-size}")
    public ResponseEntity<Page<RegistroDto>> findAll(
            @RequestParam(value = "search", required = false, defaultValue = "id>0") String search,
            @PathVariable Integer offset, @PathVariable(value = "page-size") Integer pageSize) {
        Page<RegistroDto> r = serv.findAll(search, offset, pageSize);
        log.info("Lista di registri restituita correttamente");
        return new ResponseEntity<>(r, HttpStatus.OK);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get di un registro dal suo id")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Trovato il registro", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = RegistroDto.class)) }) })
    @GetMapping("/registro/{id}")
    public ResponseEntity<RegistroDto> findById(@PathVariable Integer id) {
        RegistroDto r = serv.findById(id);
        log.info("Registro restituito correttamente");
        return new ResponseEntity<>(r, HttpStatus.OK);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Post di un registro")
    @ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Inserito nuovo registro", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = RegistroDto.class)) }) })
    @PostMapping("/registro")
    public ResponseEntity<RegistroDto> create(@Valid @RequestBody RegistroDto r) {
        RegistroDto registroDto = serv.create(r);
        log.info("Registro creato correttamente");
        log.debug("Registro creato correttamente {}", registroDto.toString());
        return new ResponseEntity<>(registroDto, HttpStatus.CREATED);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Patch di un registro")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Modifica effettuata", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = RegistroDto.class)) }) })
    @PatchMapping("/registro/{id}")
    public ResponseEntity<RegistroDto> update(@PathVariable Integer id, @Valid @RequestBody RegistroDto r) {
        RegistroDto registroDto = serv.update(id, r);
        log.info("Registro aggiornato correttamente");
        log.debug("Registro aggiornato correttamente {}", registroDto.toString());
        return new ResponseEntity<>(registroDto, HttpStatus.OK);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete di un registro")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Registro cancellato") })
    @DeleteMapping("/registro/{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id) {
        serv.delete(id);
        log.info("Registro eliminato correttamente");
        return new ResponseEntity<>(HttpStatus.OK);
    }
        
}
