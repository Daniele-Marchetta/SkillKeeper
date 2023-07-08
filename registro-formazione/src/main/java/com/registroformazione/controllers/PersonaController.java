package com.registroformazione.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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
import com.registroformazione.dto.PersonaDto;
import com.registroformazione.service.PersonaService;

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
public class PersonaController {
	@Autowired
	private PersonaService serv;

	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Get di tutte le persone")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Restituita la lista", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = PersonaDto.class))}),
			@ApiResponse(responseCode = "204", description = "Lista vuota")})
	@GetMapping("/persone/{offset}/{page-size}")
	public ResponseEntity<Page<PersonaDto>> findAll( @RequestParam(value = "search", required = false, defaultValue = "id>0") String search, @PathVariable Integer offset, @PathVariable(value = "page-size" ) Integer pageSize) {
		Page<PersonaDto> p = serv.findAll(search,offset, pageSize);
		log.info("Lista di persone restituita correttamente");
		return new ResponseEntity<>(p, HttpStatus.OK);
	}

	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Get di una persona dal suo id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Trovata la persona", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = PersonaDto.class)) })})
	@GetMapping("/persone/{id}")
	public ResponseEntity<PersonaDto> findById(@PathVariable Integer id) {
		PersonaDto p = serv.findById(id);
		log.info("Persona restituita correttamente");
		return new ResponseEntity<>(p, HttpStatus.OK);
	}

	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Post di una persona")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Inserita nuova persona", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = PersonaDto.class))})})
	@PostMapping("/persone")
	public ResponseEntity<PersonaDto> create(@Valid @RequestBody PersonaDto p) {
		PersonaDto personaDto = serv.create(p);
		log.info("Persona creata correttamente");
		log.debug("Persona creata correttamente {}",personaDto.toString());
		return new ResponseEntity<>(personaDto, HttpStatus.CREATED);
	}

	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Patch di una persona")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Modifica effettuata", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = PersonaDto.class)) })})
	@PatchMapping("persone/{id}")
	public ResponseEntity<PersonaDto> update(@PathVariable Integer id, @Valid @RequestBody PersonaDto p) {
		PersonaDto personaDto = serv.update(id, p);
		log.info("Persona creata correttamente");
		log.debug("Persona creata correttamente {}",personaDto.toString());
		return new ResponseEntity<>(personaDto, HttpStatus.OK);
	}

	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Delete di una persona")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Persona cancellata")})
	@DeleteMapping("/persone/{id}")
	public ResponseEntity<Object> delete(@PathVariable Integer id) {
		serv.delete(id);
		log.info("persona eliminata correttamente");
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
