package com.registroformazione.persone;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
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
import com.registroformazione.controllers.PersonaController;
import com.registroformazione.dto.PersonaDto;
import com.registroformazione.exceptions.DatabaseException;
import com.registroformazione.exceptions.NoDataFoundException;
import com.registroformazione.exceptions.ResourceNotFoundException;
import com.registroformazione.model.Persona;
import com.registroformazione.model.Area;
import com.registroformazione.model.Cc;

import com.registroformazione.security.config.JwtAuthenticationFilter;
import com.registroformazione.service.PersonaService;

@WebMvcTest(controllers = PersonaController.class)
@AutoConfigureMockMvc(addFilters = false) // disabilito i filtri di sicurezza sui controller
 class PersonaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonaService personaService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtAuthenticationFilter authFilter;

    private Persona persona=new Persona();
    private PersonaDto personaDto=new PersonaDto();
    private Area area = new Area();
    private Cc cc = new Cc();

    @BeforeEach
    public void init() {
        this.personaDto = new PersonaDto("Mario", "Galli", false, 1, 2);
        this.persona = new Persona(null, "Mario", "Galli", false, false, cc, null, area);
    }

    @Test
    void personaControllerFindAllReturnList() throws Exception {
        
        List<PersonaDto> currentPage = new ArrayList<>();
        currentPage.add(personaDto);
        currentPage.add(personaDto);
        currentPage.add(personaDto);
        currentPage.add(personaDto);
        
        Page<PersonaDto> returnedPage = new PageImpl<>(currentPage, PageRequest.of(1, 4), 4);
        when(personaService.findAll("nome:Mario", 1, 4)).thenReturn(returnedPage);
        
        mockMvc.perform(get("/api/persone/1/4?search=nome:Mario")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(4));
        
        verify(personaService, times(1)).findAll("nome:Mario", 1, 4);

    }

    @Test
    void personaControllerFindAllReturnNoContent() throws Exception {
        
        when(personaService.findAll(anyString(),any(), any())).thenThrow(new NoDataFoundException());
        
        mockMvc.perform(get("/api/persone/1/4?search=nome:Mario").contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
        verify(personaService,times(1)).findAll(anyString(),any(), any());
    }

    @Test
    void personaControllerFindByIdReturnOk() throws Exception {
        
        when(personaService.findById(1)).thenReturn(personaDto);
        
        ResultActions response = mockMvc.perform(get("/api/persone/1").contentType(MediaType.APPLICATION_JSON));
        
         response.andExpect(MockMvcResultMatchers.status().isOk())
         .andExpect(MockMvcResultMatchers.jsonPath("$.nome", CoreMatchers.is(personaDto.getNome())));
         verify(personaService,times(1)).findById(1);
    }

    @Test
    void personaControllerFindByIdReturnResourceNotFound() throws Exception {
        
        when(personaService.findById(1)).thenThrow(new ResourceNotFoundException("risorsa non trovata"));
        
        ResultActions response = mockMvc.perform(get("/api/persone/1").contentType(MediaType.APPLICATION_JSON));
        
         response.andExpect(MockMvcResultMatchers.status().isNotFound())
         .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(404)))
         .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("risorsa non trovata")));;

         verify(personaService,times(1)).findById(1);
    }

    @Test
    void personaControllerCreateReturnCreated() throws Exception {
        
        when(personaService.create(personaDto)).thenReturn(personaDto);
        
        ResultActions response = mockMvc.perform(post("/api/persone").contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(personaDto)));
        
         response.andExpect(MockMvcResultMatchers.status().isCreated())
         .andExpect(MockMvcResultMatchers.jsonPath("$.nome", CoreMatchers.is(personaDto.getNome())));

         verify(personaService,times(1)).create(personaDto);
    }

    @Test
    void personaControllerCreateReturnConflicts() throws Exception {
        
        when(personaService.create(personaDto)).thenThrow(new DatabaseException("errore conflitti!"));
        
        ResultActions response = mockMvc.perform(post("/api/persone").contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(personaDto)));
        
         response.andExpect(MockMvcResultMatchers.status().isConflict())
         .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(409)))
         .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("errore conflitti!")));

         verify(personaService,times(1)).create(personaDto);
    }

    @Test
    void personaControllerUpdateReturnOk() throws Exception {
        
        when(personaService.update(1,personaDto)).thenReturn(personaDto);
        
        ResultActions response = mockMvc.perform(patch("/api/persone/1").contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(personaDto)));
        
         response.andExpect(MockMvcResultMatchers.status().isOk())
         .andExpect(MockMvcResultMatchers.jsonPath("$.nome", CoreMatchers.is(personaDto.getNome())));
         
         verify(personaService,times(1)).update(1, personaDto);
    }

    @Test
    void personaControllerUpdateReturnResourceNotFound() throws Exception {
        
        when(personaService.update(1,personaDto)).thenThrow(new ResourceNotFoundException("risorsa non trovata"));
        
        ResultActions response = mockMvc.perform(patch("/api/persone/1").contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(personaDto)));
        
         response.andExpect(MockMvcResultMatchers.status().isNotFound())
         .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("risorsa non trovata")))
         .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(404)));

         
         verify(personaService,times(1)).update(1, personaDto);
    }

    @Test
    void personaControllerUpdateReturnDatabaseException() throws Exception {
        
        when(personaService.update(1,personaDto)).thenThrow(new DatabaseException("conflict"));
        
        ResultActions response = mockMvc.perform(patch("/api/persone/1").contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(personaDto)));
        
         response.andExpect(MockMvcResultMatchers.status().isConflict())
         .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("conflict")))
         .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(409)));

         
         verify(personaService,times(1)).update(1, personaDto);
    }

    @Test
    void personaControllerDeleteReturnOk() throws Exception {
        doNothing().when(personaService).delete(1);

        ResultActions response = mockMvc.perform(delete("/api/persone/1").contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk());

        verify(personaService, times(1)).delete(1);
    }

    @Test
    void personaControllerDeleteReturnResourceNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("risorsa non trovata!")).when(personaService).delete(1);

        ResultActions response = mockMvc.perform(delete("/api/persone/1").contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(404)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("risorsa non trovata!")));

        verify(personaService, times(1)).delete(1);
    }

    @Test
    void personaControllerDeleteReturnResou() throws Exception {
        doThrow(new ResourceNotFoundException("risorsa non trovata!")).when(personaService).delete(1);

        ResultActions response = mockMvc.perform(delete("/api/persone/1").contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(404)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("risorsa non trovata!")));

        verify(personaService, times(1)).delete(1);
    }

    void personaControllerDeleteReturnDatabaseException() throws Exception {
        doThrow(new DatabaseException("conflicts!")).when(personaService).delete(1);

        ResultActions response = mockMvc.perform(delete("/api/persone/1").contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(409)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("conflicts!")));

        verify(personaService, times(1)).delete(1);
    }

    // DA TESTARE
    void areaControllerhandleMethodArgumentNotValid() throws Exception {

    }

}
