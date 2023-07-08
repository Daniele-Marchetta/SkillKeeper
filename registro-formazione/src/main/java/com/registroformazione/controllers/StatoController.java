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

import com.registroformazione.dto.StatoDto;
import com.registroformazione.model.Stato;
import com.registroformazione.service.StatoService;

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
public class StatoController {

	@Autowired
	private StatoService serv;


	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Get di tutti gli stati")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Restituito lo Stato", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = StatoDto.class))}),
			@ApiResponse(responseCode = "204", description = "La lista è vuota")})
	@GetMapping("/stati")
	public ResponseEntity<List<StatoDto>> findAll() {
		List<StatoDto>s=serv.findAll();
		log.info("Lista di stati restituita correttamente");
		return new ResponseEntity<>(s, HttpStatus.OK);
	}
	
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Get di uno stato dal suo id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Trovato lo stato", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = StatoDto.class)) }) })
	@GetMapping("/stati/{id}")
	public ResponseEntity<StatoDto> findById(@PathVariable Integer id) {
		StatoDto s = serv.findById(id);
		log.info("Stato restituito correttamente");
		return new ResponseEntity<>(s,HttpStatus.OK);
	}
	
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Post di uno stato")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Inserito nuovo stato", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = StatoDto.class))}) })
	@PostMapping("/stati")
	public ResponseEntity<StatoDto> create(@Valid @RequestBody StatoDto s) {
		StatoDto statoDto = serv.create(s);
		log.info("Stato creato correttamente");
		log.debug("Stato creato correttamente {}",statoDto.toString());
		return new ResponseEntity<>(statoDto,HttpStatus.CREATED);
	}
	
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Patch di uno stato")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Modifica effettuata", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = StatoDto.class)) })})
	@PatchMapping("/stati/{id}")
	public ResponseEntity<StatoDto> update(@PathVariable Integer id, @RequestBody StatoDto s) {
		StatoDto statoDto = serv.update(id, s);
		log.info("Stato aggiornato correttamente");
		log.debug("Stato aggiornato correttamente {}",statoDto.toString());
		return new ResponseEntity<>(statoDto,HttpStatus.OK);
	}
	
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Delete di uno stato")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Stato cancellato")})
	@DeleteMapping("/stati/{id}")
	public ResponseEntity<Object> delete (@PathVariable Integer id){
		serv.delete(id);
		log.info("Stato eliminato correttamente");
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
