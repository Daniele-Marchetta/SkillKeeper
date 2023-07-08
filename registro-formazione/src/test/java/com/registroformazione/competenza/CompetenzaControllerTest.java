package com.registroformazione.competenza;

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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.registroformazione.controllers.CompetenzaController;
import com.registroformazione.dto.CompetenzaDto;
import com.registroformazione.exceptions.DatabaseException;
import com.registroformazione.exceptions.NoDataFoundException;
import com.registroformazione.exceptions.ResourceNotFoundException;
import com.registroformazione.model.Competenza;
import com.registroformazione.security.config.JwtAuthenticationFilter;
import com.registroformazione.service.CompetenzaService;

@WebMvcTest(controllers = CompetenzaController.class)
@AutoConfigureMockMvc(addFilters = false,print = MockMvcPrint.SYSTEM_OUT)
@ExtendWith(MockitoExtension.class)
class CompetenzaControllerTest {
    

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompetenzaService competenzaService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean 
    private JwtAuthenticationFilter authFilter;
   
    

    private Competenza competenza = new Competenza();
    private CompetenzaDto competenzaDto = new CompetenzaDto();

    @BeforeEach
    public void init() {
        competenzaDto.setNome("Servizi Aws");

        competenza.setId(1);
        competenza.setNome("Servizi Aws");
    }

    @Test
    void competenzaControllerFindAllReturnList() throws Exception {

        List<CompetenzaDto> returnedList = new ArrayList<>();
        returnedList.add(competenzaDto);
        returnedList.add(competenzaDto);
        returnedList.add(competenzaDto);

        when(competenzaService.findAll()).thenReturn(returnedList);

        mockMvc.perform(get("/api/competenze").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray()).andExpect(jsonPath("$.length()").value(3));
        verify(competenzaService, times(1)).findAll();
    }

    @Test
    void competenzaControllerFindAllReturnNoContent() throws Exception {
        
        when(competenzaService.findAll()).thenThrow(new NoDataFoundException());
        
        mockMvc.perform(get("/api/competenze").contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
        verify(competenzaService,times(1)).findAll();
    }

    @Test
    void competenzaControllerFindByIdReturnOk() throws Exception {
        
        when(competenzaService.findById(1)).thenReturn(competenzaDto);
        
        ResultActions response = mockMvc.perform(get("/api/competenze/1").contentType(MediaType.APPLICATION_JSON));
        
         response.andExpect(MockMvcResultMatchers.status().isOk())
         .andExpect(MockMvcResultMatchers.jsonPath("$.nome", CoreMatchers.is(competenzaDto.getNome())));
         verify(competenzaService,times(1)).findById(1);
    }

    @Test
    void competenzaControllerFindByIdReturnResourceNotFound() throws Exception {
        
        when(competenzaService.findById(1)).thenThrow(new ResourceNotFoundException("risorsa non trovata"));
        
        ResultActions response = mockMvc.perform(get("/api/competenze/1").contentType(MediaType.APPLICATION_JSON));
        
         response.andExpect(MockMvcResultMatchers.status().isNotFound())
         .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(404)))
         .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("risorsa non trovata")));;

         verify(competenzaService,times(1)).findById(1);
    }

    @Test
    void competenzaControllerCreateReturnCreated() throws Exception {
        
        when(competenzaService.create(competenzaDto)).thenReturn(competenzaDto);
        
        ResultActions response = mockMvc.perform(post("/api/competenze").contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(competenzaDto)));
        
         response.andExpect(MockMvcResultMatchers.status().isCreated())
         .andExpect(MockMvcResultMatchers.jsonPath("$.nome", CoreMatchers.is(competenzaDto.getNome())));

         verify(competenzaService,times(1)).create(competenzaDto);
    }

    @Test
    void competenzaControllerCreateReturnConflicts() throws Exception {
        
        when(competenzaService.create(competenzaDto)).thenThrow(new DatabaseException("errore conflitti!"));
        
        ResultActions response = mockMvc.perform(post("/api/competenze").contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(competenzaDto)));
        
         response.andExpect(MockMvcResultMatchers.status().isConflict())
         .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(409)))
         .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("errore conflitti!")));

         verify(competenzaService,times(1)).create(competenzaDto);
    }

    @Test
    void competenzaControllerUpdateReturnOk() throws Exception {
        
        when(competenzaService.update(1,competenzaDto)).thenReturn(competenzaDto);
        
        ResultActions response = mockMvc.perform(patch("/api/competenze/1").contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(competenzaDto)));
        
         response.andExpect(MockMvcResultMatchers.status().isOk())
         .andExpect(MockMvcResultMatchers.jsonPath("$.nome", CoreMatchers.is(competenzaDto.getNome())));
         
         verify(competenzaService,times(1)).update(1, competenzaDto);
    }

    @Test
    void competenzaControllerUpdateReturnResourceNotFound() throws Exception {
        
        when(competenzaService.update(1,competenzaDto)).thenThrow(new ResourceNotFoundException("risorsa non trovata"));
        
        ResultActions response = mockMvc.perform(patch("/api/competenze/1").contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(competenzaDto)));
        
         response.andExpect(MockMvcResultMatchers.status().isNotFound())
         .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("risorsa non trovata")))
         .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(404)));

         
         verify(competenzaService,times(1)).update(1, competenzaDto);
    }

    @Test
    void competenzaControllerUpdateReturnDatabaseException() throws Exception {
        
        when(competenzaService.update(1,competenzaDto)).thenThrow(new DatabaseException("conflict"));
        
        ResultActions response = mockMvc.perform(patch("/api/competenze/1").contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(competenzaDto)));
        
         response.andExpect(MockMvcResultMatchers.status().isConflict())
         .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("conflict")))
         .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(409)));

         
         verify(competenzaService,times(1)).update(1, competenzaDto);
    }

    @Test
    void competenzaControllerDeleteReturnOk() throws Exception {
        doNothing().when(competenzaService).delete(1);

        ResultActions response = mockMvc.perform(delete("/api/competenze/1").contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk());

        verify(competenzaService, times(1)).delete(1);
    }

    @Test
    void competenzaControllerDeleteReturnResourceNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("risorsa non trovata!")).when(competenzaService).delete(1);

        ResultActions response = mockMvc.perform(delete("/api/competenze/1").contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(404)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("risorsa non trovata!")));

        verify(competenzaService, times(1)).delete(1);
    }

    @Test
    void competenzaControllerDeleteReturnResou() throws Exception {
        doThrow(new ResourceNotFoundException("risorsa non trovata!")).when(competenzaService).delete(1);

        ResultActions response = mockMvc.perform(delete("/api/competenze/1").contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(404)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("risorsa non trovata!")));

        verify(competenzaService, times(1)).delete(1);
    }

    void competenzaControllerDeleteReturnDatabaseException() throws Exception {
        doThrow(new DatabaseException("conflicts!")).when(competenzaService).delete(1);

        ResultActions response = mockMvc.perform(delete("/api/competenze/1").contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(409)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("conflicts!")));

        verify(competenzaService, times(1)).delete(1);
    }

    
    //DA TESTARE
    void competenzaControllerhandleMethodArgumentNotValid() throws Exception {
        
    }

}
