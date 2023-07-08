package com.registroformazione.cc;

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
import com.registroformazione.controllers.CcController;
import com.registroformazione.dto.CcDto;
import com.registroformazione.exceptions.DatabaseException;
import com.registroformazione.exceptions.NoDataFoundException;
import com.registroformazione.exceptions.ResourceNotFoundException;
import com.registroformazione.model.Cc;
import com.registroformazione.security.config.JwtAuthenticationFilter;
import com.registroformazione.service.CcService;

@WebMvcTest(controllers = CcController.class)
@AutoConfigureMockMvc(addFilters = false,print = MockMvcPrint.SYSTEM_OUT)
@ExtendWith(MockitoExtension.class)
class CcControllerTest {
    

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CcService ccService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean 
    private JwtAuthenticationFilter authFilter;
   
    

    private Cc cc = new Cc();
    private CcDto ccDto = new CcDto();

    @BeforeEach
    public void init() {
        ccDto.setNome("Aws");

        cc.setId(1);
        cc.setNome("Aws");
    }

    @Test
    void ccControllerFindAllReturnList() throws Exception {

        List<CcDto> returnedList = new ArrayList<>();
        returnedList.add(ccDto);
        returnedList.add(ccDto);
        returnedList.add(ccDto);

        when(ccService.findAll()).thenReturn(returnedList);

        mockMvc.perform(get("/api/cc").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray()).andExpect(jsonPath("$.length()").value(3));
        verify(ccService, times(1)).findAll();
    }

    @Test
    void ccControllerFindAllReturnNoContent() throws Exception {
        
        when(ccService.findAll()).thenThrow(new NoDataFoundException());
        
        mockMvc.perform(get("/api/cc").contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
        verify(ccService,times(1)).findAll();
    }

    @Test
    void ccControllerFindByIdReturnOk() throws Exception {
        
        when(ccService.findById(1)).thenReturn(ccDto);
        
        ResultActions response = mockMvc.perform(get("/api/cc/1").contentType(MediaType.APPLICATION_JSON));
        
         response.andExpect(MockMvcResultMatchers.status().isOk())
         .andExpect(MockMvcResultMatchers.jsonPath("$.nome", CoreMatchers.is(ccDto.getNome())));
         verify(ccService,times(1)).findById(1);
    }

    @Test
    void ccControllerFindByIdReturnResourceNotFound() throws Exception {
        
        when(ccService.findById(1)).thenThrow(new ResourceNotFoundException("risorsa non trovata"));
        
        ResultActions response = mockMvc.perform(get("/api/cc/1").contentType(MediaType.APPLICATION_JSON));
        
         response.andExpect(MockMvcResultMatchers.status().isNotFound())
         .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(404)))
         .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("risorsa non trovata")));;

         verify(ccService,times(1)).findById(1);
    }

    @Test
    void ccControllerCreateReturnCreated() throws Exception {
        
        when(ccService.create(ccDto)).thenReturn(ccDto);
        
        ResultActions response = mockMvc.perform(post("/api/cc").contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(ccDto)));
        
         response.andExpect(MockMvcResultMatchers.status().isCreated())
         .andExpect(MockMvcResultMatchers.jsonPath("$.nome", CoreMatchers.is(ccDto.getNome())));

         verify(ccService,times(1)).create(ccDto);
    }

    @Test
    void ccControllerCreateReturnConflicts() throws Exception {
        
        when(ccService.create(ccDto)).thenThrow(new DatabaseException("errore conflitti!"));
        
        ResultActions response = mockMvc.perform(post("/api/cc").contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(ccDto)));
        
         response.andExpect(MockMvcResultMatchers.status().isConflict())
         .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(409)))
         .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("errore conflitti!")));

         verify(ccService,times(1)).create(ccDto);
    }

    @Test
    void ccControllerUpdateReturnOk() throws Exception {
        
        when(ccService.update(1,ccDto)).thenReturn(ccDto);
        
        ResultActions response = mockMvc.perform(patch("/api/cc/1").contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(ccDto)));
        
         response.andExpect(MockMvcResultMatchers.status().isOk())
         .andExpect(MockMvcResultMatchers.jsonPath("$.nome", CoreMatchers.is(ccDto.getNome())));
         
         verify(ccService,times(1)).update(1, ccDto);
    }

    @Test
    void ccControllerUpdateReturnResourceNotFound() throws Exception {
        
        when(ccService.update(1,ccDto)).thenThrow(new ResourceNotFoundException("risorsa non trovata"));
        
        ResultActions response = mockMvc.perform(patch("/api/cc/1").contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(ccDto)));
        
         response.andExpect(MockMvcResultMatchers.status().isNotFound())
         .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("risorsa non trovata")))
         .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(404)));

         
         verify(ccService,times(1)).update(1, ccDto);
    }

    @Test
    void ccControllerUpdateReturnDatabaseException() throws Exception {
        
        when(ccService.update(1,ccDto)).thenThrow(new DatabaseException("conflict"));
        
        ResultActions response = mockMvc.perform(patch("/api/cc/1").contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(ccDto)));
        
         response.andExpect(MockMvcResultMatchers.status().isConflict())
         .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("conflict")))
         .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(409)));

         
         verify(ccService,times(1)).update(1, ccDto);
    }

    @Test
    void ccControllerDeleteReturnOk() throws Exception {
        doNothing().when(ccService).delete(1);

        ResultActions response = mockMvc.perform(delete("/api/cc/1").contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk());

        verify(ccService, times(1)).delete(1);
    }

    @Test
    void ccControllerDeleteReturnResourceNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("risorsa non trovata!")).when(ccService).delete(1);

        ResultActions response = mockMvc.perform(delete("/api/cc/1").contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(404)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("risorsa non trovata!")));

        verify(ccService, times(1)).delete(1);
    }

    @Test
    void ccControllerDeleteReturnResou() throws Exception {
        doThrow(new ResourceNotFoundException("risorsa non trovata!")).when(ccService).delete(1);

        ResultActions response = mockMvc.perform(delete("/api/cc/1").contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(404)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("risorsa non trovata!")));

        verify(ccService, times(1)).delete(1);
    }

    void ccControllerDeleteReturnDatabaseException() throws Exception {
        doThrow(new DatabaseException("conflicts!")).when(ccService).delete(1);

        ResultActions response = mockMvc.perform(delete("/api/cc/1").contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(409)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("conflicts!")));

        verify(ccService, times(1)).delete(1);
    }

    
    //DA TESTARE
    void ccControllerhandleMethodArgumentNotValid() throws Exception {
        
    }

}
