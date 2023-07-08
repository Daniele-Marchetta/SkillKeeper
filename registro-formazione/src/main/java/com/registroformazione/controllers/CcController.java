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

import com.registroformazione.dto.CcDto;
import com.registroformazione.model.Cc;
import com.registroformazione.service.CcService;

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
public class CcController {
	
	@Autowired
	private CcService serv;

	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "get di tutti i centri competenza")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ritorna lista di centri competenza", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = CcDto.class)) }),
			@ApiResponse(responseCode = "204", description = "La lista è vuota") })
	@GetMapping("/cc")
	public ResponseEntity<List<CcDto>> findAll() {
		List<CcDto> ccList = serv.findAll();
		log.info("Lista centro di competenze ritornata correttamente:");
		return new ResponseEntity<>(ccList, HttpStatus.OK);
	}
	
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "get di un singolo centro di competenza")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ritorna centro di competenza", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = CcDto.class)) }) })
	@GetMapping("/cc/{id}")
	public ResponseEntity<CcDto> findById(@PathVariable Integer id) {
		CcDto cc = serv.findById(id);
		log.info("centro di competenza ritornato correttamente:");
		return new ResponseEntity<>(cc,HttpStatus.OK);
	}
	
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "inserimento centro di competenza")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "centro di competenza creato", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = CcDto.class)) }) })
	@PostMapping("/cc")
	public ResponseEntity<CcDto> create(@RequestBody @Valid CcDto ccDto) {
		CcDto cc = serv.create(ccDto);
		log.info("Cc creato correttamente");
		log.debug("Cc creato correttamente: "+cc.toString());
		return new ResponseEntity<>(cc,HttpStatus.CREATED);
	}
	
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "update centro di competenza")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "centro di competenza aggirnato", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = CcDto.class)) })})
	@PatchMapping("/cc/{id}")
	public ResponseEntity<CcDto> update(@PathVariable Integer id, @RequestBody @Valid CcDto ccDto) {
		CcDto cc=serv.update(id, ccDto);
		log.info("Cc aggiornato correttamente");
		log.debug("Cc aggiornato correttamente: "+cc.toString());
		return new ResponseEntity<>(cc,HttpStatus.OK);
	}
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "delete centro di competenza")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Centro di competenza elliminato")})
	@DeleteMapping("/cc/{id}")
	public ResponseEntity<Object> delete (@PathVariable Integer id){
		serv.delete(id);
		log.info("Cc cancellata correttamente.");
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
