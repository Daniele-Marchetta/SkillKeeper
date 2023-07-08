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

import com.registroformazione.dto.AreaDto;
import com.registroformazione.service.AreaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/api")
public class AreaController {

	@Autowired
	private AreaService serv;

	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Get di tutte le aree")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Restituita la lista", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = AreaDto.class)) }),
			@ApiResponse(responseCode = "204", description = "La lista è vuota") })
	@GetMapping("/aree")
	public ResponseEntity<List<AreaDto>> findAll() {
		List<AreaDto> aree = serv.findAll();
		log.info("Restituita lista di aree di appartenenza");
		return new ResponseEntity<>(aree, HttpStatus.OK);
	}

    @SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Get di una singola area")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "restituita singola area", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = AreaDto.class)) }) })
	@GetMapping("/aree/{id}")
	public ResponseEntity<AreaDto> findById(@PathVariable Integer id) {
		AreaDto area = serv.findById(id);
		log.info("Restituita singola area di appartenenza");
		return new ResponseEntity<>(area, HttpStatus.OK);
	}

    @SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "inserimento di un'area")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "restituita singola area", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = AreaDto.class)) }) })
	@PostMapping("/aree")
	public ResponseEntity<AreaDto> create(@RequestBody @Valid AreaDto a) {
		AreaDto area = serv.create(a);
		log.info("Area creata correttamente");
		log.debug("Area creata correttamente: " + area.toString());
		return new ResponseEntity<>(area, HttpStatus.CREATED);
	}

    @SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "update di una singola area")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "restituito il record aggiornato della singola area", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = AreaDto.class)) }) })
	@PatchMapping("/aree/{id}")
	public ResponseEntity<AreaDto> update(@PathVariable Integer id, @RequestBody @Valid AreaDto a) {
		AreaDto area = serv.update(id, a);
		log.info("Area aggiornata correttamente");
		return new ResponseEntity<>(area, HttpStatus.OK);
	}

    @SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Delete di un'area")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Elliminazione avvenuta con successo") })
	@DeleteMapping("/aree/{id}")
	public ResponseEntity<Object> delete(@PathVariable Integer id) {
		serv.delete(id);
		log.info("Area cancellata correttamente.");
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
