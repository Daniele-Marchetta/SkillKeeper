package com.registroformazione.vendor;

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
import com.registroformazione.controllers.VendorController;
import com.registroformazione.dto.VendorDto;
import com.registroformazione.exceptions.DatabaseException;
import com.registroformazione.exceptions.NoDataFoundException;
import com.registroformazione.exceptions.ResourceNotFoundException;
import com.registroformazione.model.Vendor;
import com.registroformazione.security.config.JwtAuthenticationFilter;
import com.registroformazione.service.VendorService;

@WebMvcTest(controllers = VendorController.class)
@AutoConfigureMockMvc(addFilters = false,print = MockMvcPrint.SYSTEM_OUT)
@ExtendWith(MockitoExtension.class)
class VendorControllerTest {
    

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VendorService vendorService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean 
    private JwtAuthenticationFilter authFilter;
   
    

    private Vendor vendor = new Vendor();
    private VendorDto vendorDto = new VendorDto();

    @BeforeEach
    public void init() {
        vendorDto.setNome("Amazon");

        vendor.setId(1);
        vendor.setNome("Amazon");
    }

    @Test
    void vendorControllerFindAllReturnList() throws Exception {

        List<VendorDto> returnedList = new ArrayList<>();
        returnedList.add(vendorDto);
        returnedList.add(vendorDto);
        returnedList.add(vendorDto);

        when(vendorService.findAll()).thenReturn(returnedList);

        mockMvc.perform(get("/api/vendors").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray()).andExpect(jsonPath("$.length()").value(3));
        verify(vendorService, times(1)).findAll();
    }

    @Test
    void vendorControllerFindAllReturnNoContent() throws Exception {
        
        when(vendorService.findAll()).thenThrow(new NoDataFoundException());
        
        mockMvc.perform(get("/api/vendors").contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
        verify(vendorService,times(1)).findAll();
    }

    @Test
    void vendorControllerFindByIdReturnOk() throws Exception {
        
        when(vendorService.findById(1)).thenReturn(vendorDto);
        
        ResultActions response = mockMvc.perform(get("/api/vendors/1").contentType(MediaType.APPLICATION_JSON));
        
         response.andExpect(MockMvcResultMatchers.status().isOk())
         .andExpect(MockMvcResultMatchers.jsonPath("$.nome", CoreMatchers.is(vendorDto.getNome())));
         verify(vendorService,times(1)).findById(1);
    }

    @Test
    void vendorControllerFindByIdReturnResourceNotFound() throws Exception {
        
        when(vendorService.findById(1)).thenThrow(new ResourceNotFoundException("risorsa non trovata"));
        
        ResultActions response = mockMvc.perform(get("/api/vendors/1").contentType(MediaType.APPLICATION_JSON));
        
         response.andExpect(MockMvcResultMatchers.status().isNotFound())
         .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(404)))
         .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("risorsa non trovata")));;

         verify(vendorService,times(1)).findById(1);
    }

    @Test
    void vendorControllerCreateReturnCreated() throws Exception {
        
        when(vendorService.create(vendorDto)).thenReturn(vendorDto);
        
        ResultActions response = mockMvc.perform(post("/api/vendors").contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(vendorDto)));
        
         response.andExpect(MockMvcResultMatchers.status().isCreated())
         .andExpect(MockMvcResultMatchers.jsonPath("$.nome", CoreMatchers.is(vendorDto.getNome())));

         verify(vendorService,times(1)).create(vendorDto);
    }

    @Test
    void vendorControllerCreateReturnConflicts() throws Exception {
        
        when(vendorService.create(vendorDto)).thenThrow(new DatabaseException("errore conflitti!"));
        
        ResultActions response = mockMvc.perform(post("/api/vendors").contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(vendorDto)));
        
         response.andExpect(MockMvcResultMatchers.status().isConflict())
         .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(409)))
         .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("errore conflitti!")));

         verify(vendorService,times(1)).create(vendorDto);
    }

    @Test
    void vendorControllerUpdateReturnOk() throws Exception {
        
        when(vendorService.update(1,vendorDto)).thenReturn(vendorDto);
        
        ResultActions response = mockMvc.perform(patch("/api/vendors/1").contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(vendorDto)));
        
         response.andExpect(MockMvcResultMatchers.status().isOk())
         .andExpect(MockMvcResultMatchers.jsonPath("$.nome", CoreMatchers.is(vendorDto.getNome())));
         
         verify(vendorService,times(1)).update(1, vendorDto);
    }

    @Test
    void vendorControllerUpdateReturnResourceNotFound() throws Exception {
        
        when(vendorService.update(1,vendorDto)).thenThrow(new ResourceNotFoundException("risorsa non trovata"));
        
        ResultActions response = mockMvc.perform(patch("/api/vendors/1").contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(vendorDto)));
        
         response.andExpect(MockMvcResultMatchers.status().isNotFound())
         .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("risorsa non trovata")))
         .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(404)));

         
         verify(vendorService,times(1)).update(1, vendorDto);
    }

    @Test
    void vendorControllerUpdateReturnDatabaseException() throws Exception {
        
        when(vendorService.update(1,vendorDto)).thenThrow(new DatabaseException("conflict"));
        
        ResultActions response = mockMvc.perform(patch("/api/vendors/1").contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(vendorDto)));
        
         response.andExpect(MockMvcResultMatchers.status().isConflict())
         .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("conflict")))
         .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(409)));

         
         verify(vendorService,times(1)).update(1, vendorDto);
    }

    @Test
    void vendorControllerDeleteReturnOk() throws Exception {
        doNothing().when(vendorService).delete(1);

        ResultActions response = mockMvc.perform(delete("/api/vendors/1").contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk());

        verify(vendorService, times(1)).delete(1);
    }

    @Test
    void vendorControllerDeleteReturnResourceNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("risorsa non trovata!")).when(vendorService).delete(1);

        ResultActions response = mockMvc.perform(delete("/api/vendors/1").contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(404)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("risorsa non trovata!")));

        verify(vendorService, times(1)).delete(1);
    }

    @Test
    void vendorControllerDeleteReturnResou() throws Exception {
        doThrow(new ResourceNotFoundException("risorsa non trovata!")).when(vendorService).delete(1);

        ResultActions response = mockMvc.perform(delete("/api/vendors/1").contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(404)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("risorsa non trovata!")));

        verify(vendorService, times(1)).delete(1);
    }

    void vendorControllerDeleteReturnDatabaseException() throws Exception {
        doThrow(new DatabaseException("conflicts!")).when(vendorService).delete(1);

        ResultActions response = mockMvc.perform(delete("/api/vendors/1").contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(409)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("conflicts!")));

        verify(vendorService, times(1)).delete(1);
    }

    
    //DA TESTARE
    void vendorControllerhandleMethodArgumentNotValid() throws Exception {
        
    }

}
