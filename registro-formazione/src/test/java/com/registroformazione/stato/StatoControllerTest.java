package com.registroformazione.stato;

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
import com.registroformazione.controllers.StatoController;
import com.registroformazione.dto.StatoDto;
import com.registroformazione.exceptions.DatabaseException;
import com.registroformazione.exceptions.NoDataFoundException;
import com.registroformazione.exceptions.ResourceNotFoundException;
import com.registroformazione.model.Stato;
import com.registroformazione.security.config.JwtAuthenticationFilter;
import com.registroformazione.service.StatoService;

@WebMvcTest(controllers = StatoController.class)
@AutoConfigureMockMvc(addFilters = false,print = MockMvcPrint.SYSTEM_OUT)
@ExtendWith(MockitoExtension.class)
class StatoControllerTest {
    

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatoService statoService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean 
    private JwtAuthenticationFilter authFilter;
   
    

    private Stato stato = new Stato();
    private StatoDto statoDto = new StatoDto();

    @BeforeEach
    public void init() {
        statoDto.setNome("Eseguito");

        stato.setId(1);
        stato.setNome("Eseguito");
    }

    @Test
    void statoControllerFindAllReturnList() throws Exception {

        List<StatoDto> returnedList = new ArrayList<>();
        returnedList.add(statoDto);
        returnedList.add(statoDto);
        returnedList.add(statoDto);

        when(statoService.findAll()).thenReturn(returnedList);

        mockMvc.perform(get("/api/stati").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray()).andExpect(jsonPath("$.length()").value(3));
        verify(statoService, times(1)).findAll();
    }

    @Test
    void statoControllerFindAllReturnNoContent() throws Exception {
        
        when(statoService.findAll()).thenThrow(new NoDataFoundException());
        
        mockMvc.perform(get("/api/stati").contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
        verify(statoService,times(1)).findAll();
    }

    @Test
    void statoControllerFindByIdReturnOk() throws Exception {
        
        when(statoService.findById(1)).thenReturn(statoDto);
        
        ResultActions response = mockMvc.perform(get("/api/stati/1").contentType(MediaType.APPLICATION_JSON));
        
         response.andExpect(MockMvcResultMatchers.status().isOk())
         .andExpect(MockMvcResultMatchers.jsonPath("$.nome", CoreMatchers.is(statoDto.getNome())));
         verify(statoService,times(1)).findById(1);
    }

    @Test
    void statoControllerFindByIdReturnResourceNotFound() throws Exception {
        
        when(statoService.findById(1)).thenThrow(new ResourceNotFoundException("risorsa non trovata"));
        
        ResultActions response = mockMvc.perform(get("/api/stati/1").contentType(MediaType.APPLICATION_JSON));
        
         response.andExpect(MockMvcResultMatchers.status().isNotFound())
         .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(404)))
         .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("risorsa non trovata")));;

         verify(statoService,times(1)).findById(1);
    }

    @Test
    void statoControllerCreateReturnCreated() throws Exception {
        
        when(statoService.create(statoDto)).thenReturn(statoDto);
        
        ResultActions response = mockMvc.perform(post("/api/stati").contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(statoDto)));
        
         response.andExpect(MockMvcResultMatchers.status().isCreated())
         .andExpect(MockMvcResultMatchers.jsonPath("$.nome", CoreMatchers.is(statoDto.getNome())));

         verify(statoService,times(1)).create(statoDto);
    }

    @Test
    void statoControllerCreateReturnConflicts() throws Exception {
        
        when(statoService.create(statoDto)).thenThrow(new DatabaseException("errore conflitti!"));
        
        ResultActions response = mockMvc.perform(post("/api/stati").contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(statoDto)));
        
         response.andExpect(MockMvcResultMatchers.status().isConflict())
         .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(409)))
         .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("errore conflitti!")));

         verify(statoService,times(1)).create(statoDto);
    }

    @Test
    void statoControllerUpdateReturnOk() throws Exception {
        
        when(statoService.update(1,statoDto)).thenReturn(statoDto);
        
        ResultActions response = mockMvc.perform(patch("/api/stati/1").contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(statoDto)));
        
         response.andExpect(MockMvcResultMatchers.status().isOk())
         .andExpect(MockMvcResultMatchers.jsonPath("$.nome", CoreMatchers.is(statoDto.getNome())));
         
         verify(statoService,times(1)).update(1, statoDto);
    }

    @Test
    void statoControllerUpdateReturnResourceNotFound() throws Exception {
        
        when(statoService.update(1,statoDto)).thenThrow(new ResourceNotFoundException("risorsa non trovata"));
        
        ResultActions response = mockMvc.perform(patch("/api/stati/1").contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(statoDto)));
        
         response.andExpect(MockMvcResultMatchers.status().isNotFound())
         .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("risorsa non trovata")))
         .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(404)));

         
         verify(statoService,times(1)).update(1, statoDto);
    }

    @Test
    void statoControllerUpdateReturnDatabaseException() throws Exception {
        
        when(statoService.update(1,statoDto)).thenThrow(new DatabaseException("conflict"));
        
        ResultActions response = mockMvc.perform(patch("/api/stati/1").contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(statoDto)));
        
         response.andExpect(MockMvcResultMatchers.status().isConflict())
         .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("conflict")))
         .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(409)));

         
         verify(statoService,times(1)).update(1, statoDto);
    }

    @Test
    void statoControllerDeleteReturnOk() throws Exception {
        doNothing().when(statoService).delete(1);

        ResultActions response = mockMvc.perform(delete("/api/stati/1").contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk());

        verify(statoService, times(1)).delete(1);
    }

    @Test
    void statoControllerDeleteReturnResourceNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("risorsa non trovata!")).when(statoService).delete(1);

        ResultActions response = mockMvc.perform(delete("/api/stati/1").contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(404)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("risorsa non trovata!")));

        verify(statoService, times(1)).delete(1);
    }

    @Test
    void statoControllerDeleteReturnResou() throws Exception {
        doThrow(new ResourceNotFoundException("risorsa non trovata!")).when(statoService).delete(1);

        ResultActions response = mockMvc.perform(delete("/api/stati/1").contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(404)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("risorsa non trovata!")));

        verify(statoService, times(1)).delete(1);
    }

    void statoControllerDeleteReturnDatabaseException() throws Exception {
        doThrow(new DatabaseException("conflicts!")).when(statoService).delete(1);

        ResultActions response = mockMvc.perform(delete("/api/stati/1").contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(409)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("conflicts!")));

        verify(statoService, times(1)).delete(1);
    }

    
    //DA TESTARE
    void statoControllerhandleMethodArgumentNotValid() throws Exception {
        
    }

}
