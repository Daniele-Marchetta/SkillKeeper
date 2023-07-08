package com.registroformazione.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.registroformazione.model.ExcelImportErrorLog;
import com.registroformazione.model.ExcelValidationErrors;
import com.registroformazione.service.ExcelService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/api")
@Log4j2
public class ExcelController {
    
    @Autowired ExcelService serv;
    
    
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Popola e crea un excel con una tabella che mostra facilmente certificazioni e persone che le hanno ottenute")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Puoi scaricare l'excel", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Resource.class)) }),
            @ApiResponse(responseCode = "204", description = "Non ci sono certificazioni eseguite del vendor richiesto") })
    // volendo possiamo gestire anche la response 404
    @GetMapping("/excel/certificazioni")
    public ResponseEntity<Resource> createExcel(@RequestParam String vendor) {
        byte[] excelBytes = serv.createExcel(vendor);
        ByteArrayResource resource = new ByteArrayResource(excelBytes);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Certificazioni.xlsx");
        return ResponseEntity.ok().headers(headers).contentLength(excelBytes.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Popola e crea un excel con una tabella che mostra le righe di registro dell'excel originale")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Puoi scaricare l'excel", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Resource.class)) }),
            @ApiResponse(responseCode = "204", description = "Non ci sono dati nel registro") })
    @GetMapping("/excel/registri")
    public ResponseEntity<Resource> createRegistriExcel() {
        byte[] excelBytes = serv.createRegistriExcel();
        ByteArrayResource resource = new ByteArrayResource(excelBytes);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=RegistroFormazione.xlsx");
        return ResponseEntity.ok().headers(headers).contentLength(excelBytes.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "upload excel su staging table")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "upload effettuato ritorno log", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ExcelImportErrorLog.class)) }) })
    @PostMapping("/excel/upload")
    public ResponseEntity<ExcelImportErrorLog> uploadFile(@RequestParam("file") MultipartFile file) {
        ExcelImportErrorLog ex= serv.uploadFile(file);
        log.info("upload excel su staging table effettuato correttamente");
        log.debug("upload excel su staging table effettuato correttamente {}",ex.toString());
        return  new ResponseEntity<>(ex, HttpStatus.OK);
    }
    
    
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "importazione dell'excel dalla staging table al db")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Inseriti dati excel in db", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ExcelImportErrorLog.class)) }) })
    @PostMapping("/excel/start-import")
    public ResponseEntity<ExcelImportErrorLog> startImport(@RequestParam("id") Integer id){
       ExcelImportErrorLog err= serv.startImport(id);
//       log.info("mapping excel db effettuato correttamente");
//        log.debug("mapping excel db effettuato correttamente {}",err.toString());
       return  new ResponseEntity<>(err, HttpStatus.OK);
    }
    
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Restituisce gli errori del file excel")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "restituita lista errori", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ExcelValidationErrors.class)) }) })
    @GetMapping("/excel/verify-errors")
    public ResponseEntity<List<ExcelValidationErrors>> verifyStagingErrors(){
       List<ExcelValidationErrors> err= serv.verifyStagingErrors();
       log.info("errori campi excel restituiti correttamente");
        log.debug("errori campi excel restituiti correttamente {}",err.toString());
       return  new ResponseEntity<>(err, HttpStatus.OK);
    }
    
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "caricato file in staging e controllo errori")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "upload effettuato ritorno lista di errori", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ExcelImportErrorLog.class)) }) })
    @PostMapping("/excel/to-schedule")
    public ResponseEntity<List<ExcelValidationErrors>> toSchedule(@RequestParam("file") MultipartFile file) {
        List<ExcelValidationErrors> ex= serv.toSchedule(file);
        log.info("full upload effettuato correttamente");
        log.debug("full upload effettuato correttamente {}",ex.toString());
        return  new ResponseEntity<>(ex, HttpStatus.OK);
    }
}
