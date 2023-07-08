package com.registroformazione.registro;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.registroformazione.controllers.RegistroController;
import com.registroformazione.dto.RegistroDto;
import com.registroformazione.exceptions.DatabaseException;
import com.registroformazione.exceptions.NoDataFoundException;
import com.registroformazione.exceptions.ResourceNotFoundException;
import com.registroformazione.model.Attivita;
import com.registroformazione.model.Persona;
import com.registroformazione.model.Registro;
import com.registroformazione.model.Stato;
import com.registroformazione.security.config.JwtAuthenticationFilter;
import com.registroformazione.service.RegistroService;

@WebMvcTest(controllers = RegistroController.class)
@AutoConfigureMockMvc(addFilters = false)//disabilito i filtri di sicurezza sui controller
 class RegistroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegistroService registroService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean 
    private JwtAuthenticationFilter authFilter;
   
    

    private Registro registro = new Registro();
    private RegistroDto registroDto = new RegistroDto();
    private Persona persona = new Persona();
    private Stato stato = new Stato();
    private Attivita attivita = new Attivita();


    @BeforeEach
    public void init() {
        registroDto.setAnno(2023);
        registroDto.setAttivitaId(1);
        registroDto.setDataCompletamento(LocalDate.of(2023, 5, 17));
        registroDto.setDataScadenza(LocalDate.of(2024, 5, 17));
        registroDto.setNota(null);
        registroDto.setPersonaId(1);
        registroDto.setStatoId(1);
        registroDto.setTipo("Certificazione");
        registro.setAnno(2023);
        registro.setAttivita(attivita);
        registro.setDataCompletamento(LocalDate.of(2023, 5, 17));
        registro.setDataScadenza(LocalDate.of(2024, 5, 17));
        registro.setNota(null);
        persona.setId(1);
        persona.setNome("Lerry");
        registro.setPersona(persona);
        registro.setStato(stato);
        registro.setTipo("Certificazione");
        
    }

    @Test
    void registroControllerFindAllReturnList() throws Exception {

        List<RegistroDto> currentPage = new ArrayList<RegistroDto>();
        currentPage.add(registroDto);
        currentPage.add(registroDto);
        currentPage.add(registroDto);
        currentPage.add(registroDto);
        Page<RegistroDto> returnedPage = new PageImpl<>(currentPage, PageRequest.of(1, 4), 4);


        when(registroService.findAll("nome:Lerry", 1, 4)).thenReturn(returnedPage);

        mockMvc.perform(get("/api/registro/1/4?search=nome:Lerry").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray()).andExpect(jsonPath("$.content.length()").value(4));
        verify(registroService, times(1)).findAll("nome:Lerry", 1, 4);
    }

    @Test
    void registroControllerFindAllReturnNoContent() throws Exception {
        
        when(registroService.findAll("nome:Daniele", 1, 4)).thenThrow(new NoDataFoundException());
        
        mockMvc.perform(get("/api/registro/1/4?search=nome:Daniele").contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
        verify(registroService,times(1)).findAll("nome:Daniele", 1, 4);
    }

    @Test
    void registroControllerFindByIdReturnOk() throws Exception {
        
        when(registroService.findById(1)).thenReturn(registroDto);
        
        ResultActions response = mockMvc.perform(get("/api/registro/1").contentType(MediaType.APPLICATION_JSON));
        
         response.andExpect(MockMvcResultMatchers.status().isOk())
         .andExpect(MockMvcResultMatchers.jsonPath("$.tipo", CoreMatchers.is(registroDto.getTipo())));
         verify(registroService,times(1)).findById(1);
    }

    @Test
    void registroControllerFindByIdReturnResourceNotFound() throws Exception {
        
        when(registroService.findById(1)).thenThrow(new ResourceNotFoundException("risorsa non trovata"));
        
        ResultActions response = mockMvc.perform(get("/api/registro/1").contentType(MediaType.APPLICATION_JSON));
        
         response.andExpect(MockMvcResultMatchers.status().isNotFound())
         .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(404)))
         .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("risorsa non trovata")));;

         verify(registroService,times(1)).findById(1);
    }

    @Test
    void registroControllerCreateReturnCreated() throws Exception {
        
        when(registroService.create(registroDto)).thenReturn(registroDto);
        
        ResultActions response = mockMvc.perform(post("/api/registro").contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(registroDto)));
        
         response.andExpect(MockMvcResultMatchers.status().isCreated())
         .andExpect(MockMvcResultMatchers.jsonPath("$.tipo", CoreMatchers.is(registroDto.getTipo())));

         verify(registroService,times(1)).create(registroDto);
    }

    @Test
    void registroControllerCreateReturnConflicts() throws Exception {
        
        when(registroService.create(registroDto)).thenThrow(new DatabaseException("errore conflitti!"));
        
        ResultActions response = mockMvc.perform(post("/api/registro").contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(registroDto)));
        
         response.andExpect(MockMvcResultMatchers.status().isConflict())
         .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(409)))
         .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("errore conflitti!")));

         verify(registroService,times(1)).create(registroDto);
    }

    @Test
    void registroControllerUpdateReturnOk() throws Exception {
        
        when(registroService.update(1,registroDto)).thenReturn(registroDto);
        
        ResultActions response = mockMvc.perform(patch("/api/registro/1").contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(registroDto)));
        
         response.andExpect(MockMvcResultMatchers.status().isOk())
         .andExpect(MockMvcResultMatchers.jsonPath("$.tipo", CoreMatchers.is(registroDto.getTipo())));
         
         verify(registroService,times(1)).update(1, registroDto);
    }

    @Test
    void registroControllerUpdateReturnResourceNotFound() throws Exception {
        
        when(registroService.update(1,registroDto)).thenThrow(new ResourceNotFoundException("risorsa non trovata"));
        
        ResultActions response = mockMvc.perform(patch("/api/registro/1").contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(registroDto)));
        
         response.andExpect(MockMvcResultMatchers.status().isNotFound())
         .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("risorsa non trovata")))
         .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(404)));

         
         verify(registroService,times(1)).update(1, registroDto);
    }

    @Test
    void registroControllerUpdateReturnDatabaseException() throws Exception {
        
        when(registroService.update(1,registroDto)).thenThrow(new DatabaseException("conflict"));
        
        ResultActions response = mockMvc.perform(patch("/api/registro/1").contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(registroDto)));
        
         response.andExpect(MockMvcResultMatchers.status().isConflict())
         .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("conflict")))
         .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(409)));

         
         verify(registroService,times(1)).update(1, registroDto);
    }
//
    @Test
    void registroControllerDeleteReturnOk() throws Exception {
        doNothing().when(registroService).delete(1);

        ResultActions response = mockMvc.perform(delete("/api/registro/1").contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk());

        verify(registroService, times(1)).delete(1);
    }

    @Test
    void registroControllerDeleteReturnResourceNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("risorsa non trovata!")).when(registroService).delete(1);

        ResultActions response = mockMvc.perform(delete("/api/registro/1").contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(404)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("risorsa non trovata!")));

        verify(registroService, times(1)).delete(1);
    }

    @Test
    void registroControllerDeleteReturnResou() throws Exception {
        doThrow(new ResourceNotFoundException("risorsa non trovata!")).when(registroService).delete(1);

        ResultActions response = mockMvc.perform(delete("/api/registro/1").contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(404)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("risorsa non trovata!")));

        verify(registroService, times(1)).delete(1);
    }

    void registroControllerDeleteReturnDatabaseException() throws Exception {
        doThrow(new DatabaseException("conflicts!")).when(registroService).delete(1);

        ResultActions response = mockMvc.perform(delete("/api/registro/1").contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(409)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("conflicts!")));

        verify(registroService, times(1)).delete(1);
    }

//    
//    //DA TESTARE
//    void registroControllerhandleMethodArgumentNotValid() throws Exception {
//        
//    }
    
}
