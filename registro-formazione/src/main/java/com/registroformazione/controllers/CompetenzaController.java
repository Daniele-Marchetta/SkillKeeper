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

import com.registroformazione.dto.CompetenzaDto;
import com.registroformazione.model.Competenza;
import com.registroformazione.service.CompetenzaService;

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
public class CompetenzaController {

	@Autowired
	private CompetenzaService serv;

	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "get di tutte competenza")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ritorna lista competenze", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = CompetenzaDto.class)) }),
			@ApiResponse(responseCode = "204", description = "La lista è vuota")})
	@GetMapping("/competenze")
	public ResponseEntity<List<CompetenzaDto>> findAll() {
		List<CompetenzaDto> competenze =serv.findAll();
		log.info("lista competenze ritornate correttamente");
		return new ResponseEntity<>(competenze, HttpStatus.OK);
	}
	
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "get di una competenza")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ritorna competenza", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = CompetenzaDto.class)) }) })

	@GetMapping("/competenze/{id}")
	public ResponseEntity<CompetenzaDto> findById(@PathVariable Integer id) {
		CompetenzaDto competenza = serv.findById(id);
		log.info("competenza ritornata correttamente");
		return new ResponseEntity<>(competenza,HttpStatus.OK);
	}
	
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "inserimento di una competenza")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "competenza inserita", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = CompetenzaDto.class)) }) })
	@PostMapping("/competenze")
	public ResponseEntity<CompetenzaDto> create(@RequestBody @Valid CompetenzaDto c) {
		CompetenzaDto competenza = serv.create(c);
		log.info("competenza creata correttamente");
		log.debug("competenza creata correttamente {}",competenza.toString());
		return new ResponseEntity<>(competenza,HttpStatus.CREATED);
	}
	
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "update di una competenza")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "competenza aggiornata", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = CompetenzaDto.class)) })
			})
	@PatchMapping("/competenze/{id}")
	public ResponseEntity<CompetenzaDto> update(@PathVariable @Valid Integer id, @RequestBody CompetenzaDto c) {
		CompetenzaDto competenza = serv.update(id, c);
		log.info("persona aggiornata correttamente");
		log.debug("persona aggiornata correttamente {}",competenza.toString());
		return new ResponseEntity<>(competenza,HttpStatus.OK);
	}
	
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "delete di una competenza")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Competenza elliminata")
			})
	@DeleteMapping("/competenze/{id}")
	public ResponseEntity<Object> delete (@PathVariable Integer id){
		serv.delete(id);
		log.info("persona eliminata correttamente");
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
