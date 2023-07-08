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

import com.registroformazione.dto.VendorDto;
import com.registroformazione.model.Vendor;
import com.registroformazione.service.VendorService;

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
public class VendorController {

	@Autowired
	private VendorService serv;

	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Get di tutti i vendor")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Restituito il vendor", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = VendorDto.class)) }),
			@ApiResponse(responseCode = "204", description = "La lista è vuota") })
	@GetMapping("/vendors")
	public ResponseEntity<List<VendorDto>> findAll() {
		List<VendorDto> v =serv.findAll();
		log.info("Lista di vendors restituita correttamente");
		return new ResponseEntity<>(v, HttpStatus.OK);
	}

	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Get di un vendor dal suo id")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Trovato il vendor", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = VendorDto.class)) }) })
	@GetMapping("/vendors/{id}")
	public ResponseEntity<VendorDto> findById(@PathVariable Integer id) {
		VendorDto v=serv.findById(id);
		log.info("Vendor restituito correttamente");
		return new ResponseEntity<>(v, HttpStatus.OK);
	}

	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Post di un vendor")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Inserito nuovo vendor", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = VendorDto.class)) }) })
	@PostMapping("/vendors")
	public ResponseEntity<VendorDto> create(@Valid @RequestBody VendorDto v) {
		VendorDto vendorDto = serv.create(v);
		log.info("Vendor creato correttamente");
		log.debug("Vendor creato correttamente {}",vendorDto.toString());
		return new ResponseEntity<>(vendorDto, HttpStatus.CREATED);
	}

	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Patch di un vendor")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Modifica effettuata", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = VendorDto.class)) }) })
	@PatchMapping("/vendors/{id}")
	public ResponseEntity<VendorDto> update(@PathVariable Integer id, @Valid @RequestBody VendorDto v) {
		VendorDto vendorDto = serv.update(id, v);
		log.info("Vendor aggiornato correttamente");
		log.debug("Vendor aggiornato correttamente {}",vendorDto.toString());
		return new ResponseEntity<>(vendorDto, HttpStatus.OK);
	}

	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Delete di un vendor")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Vendor cancellato") })
	@DeleteMapping("/vendors/{id}")
	public ResponseEntity<Object> delete(@PathVariable Integer id) {
		serv.delete(id);
		log.info("Vendor eliminato correttamente");
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
