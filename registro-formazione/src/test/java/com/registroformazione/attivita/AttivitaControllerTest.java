package com.registroformazione.attivita;

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
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.registroformazione.controllers.AttivitaController;
import com.registroformazione.dto.AttivitaDto;
import com.registroformazione.exceptions.DatabaseException;
import com.registroformazione.exceptions.NoDataFoundException;
import com.registroformazione.exceptions.ResourceNotFoundException;
import com.registroformazione.model.Attivita;
import com.registroformazione.model.Competenza;
import com.registroformazione.model.Vendor;
import com.registroformazione.security.config.JwtAuthenticationFilter;
import com.registroformazione.service.AttivitaService;

@WebMvcTest(controllers = AttivitaController.class)
@AutoConfigureMockMvc(addFilters = false)//disabilito i filtri di sicurezza sui controller
class AttivitaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AttivitaService attivitaService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean 
    private JwtAuthenticationFilter authFilter;

   
    

    private Attivita attivita = new Attivita();
    private AttivitaDto attivitaDto = new AttivitaDto();
    private Vendor vendor = new Vendor(2, "Microsoft", null);
    private Competenza competenza = new Competenza(1, "Microsoft foundamentals", null);

    @BeforeEach
    public void init() {
        attivitaDto.setNome("Microsoft foundamentals");
        attivitaDto.setCodice("z34");
        attivitaDto.setCompetenzaId(1);
        attivitaDto.setVendorId(2);

        attivita.setId(1);
        attivita.setNome("Microsoft foundamentals");
        attivita.setCodice("z34");
        attivita.setCompetenza(competenza);
        attivita.setVendor(vendor);
        
    }

    @Test
    void attivitaControllerFindAllReturnList() throws Exception {

        List<AttivitaDto> returnedList = new ArrayList<>();
        returnedList.add(attivitaDto);
        returnedList.add(attivitaDto);
        returnedList.add(attivitaDto);

        when(attivitaService.findAll()).thenReturn(returnedList);

        mockMvc.perform(get("/api/attivita").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray()).andExpect(jsonPath("$.length()").value(3));
        verify(attivitaService, times(1)).findAll();
    }

    @Test
    void attivitaControllerFindAllReturnNoContent() throws Exception {
        
        when(attivitaService.findAll()).thenThrow(new NoDataFoundException());
        
        mockMvc.perform(get("/api/attivita").contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
        verify(attivitaService,times(1)).findAll();
    }

    @Test
    void attivitaControllerFindByIdReturnOk() throws Exception {
        
        when(attivitaService.findById(1)).thenReturn(attivitaDto);
        
        ResultActions response = mockMvc.perform(get("/api/attivita/1").contentType(MediaType.APPLICATION_JSON));
        
         response.andExpect(MockMvcResultMatchers.status().isOk())
         .andExpect(MockMvcResultMatchers.jsonPath("$.nome", CoreMatchers.is(attivitaDto.getNome())));
         verify(attivitaService,times(1)).findById(1);
    }

    @Test
    void attivitaControllerFindByIdReturnResourceNotFound() throws Exception {
        
        when(attivitaService.findById(1)).thenThrow(new ResourceNotFoundException("risorsa non trovata"));
        
        ResultActions response = mockMvc.perform(get("/api/attivita/1").contentType(MediaType.APPLICATION_JSON));
        
         response.andExpect(MockMvcResultMatchers.status().isNotFound())
         .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(404)))
         .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("risorsa non trovata")));;

         verify(attivitaService,times(1)).findById(1);
    }

    @Test
    void attivitaControllerCreateReturnCreated() throws Exception {
        
        when(attivitaService.create(attivitaDto)).thenReturn(attivitaDto);
        
        ResultActions response = mockMvc.perform(post("/api/attivita").contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(attivitaDto)));
        
         response.andExpect(MockMvcResultMatchers.status().isCreated())
         .andExpect(MockMvcResultMatchers.jsonPath("$.nome", CoreMatchers.is(attivitaDto.getNome())));

         verify(attivitaService,times(1)).create(attivitaDto);
    }

    @Test
    void attivitaControllerCreateReturnConflicts() throws Exception {
        
        when(attivitaService.create(attivitaDto)).thenThrow(new DatabaseException("errore conflitti!"));
        
        ResultActions response = mockMvc.perform(post("/api/attivita").contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(attivitaDto)));
        
         response.andExpect(MockMvcResultMatchers.status().isConflict())
         .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(409)))
         .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("errore conflitti!")));

         verify(attivitaService,times(1)).create(attivitaDto);
    }

    @Test
    void attivitaControllerUpdateReturnOk() throws Exception {
        
        when(attivitaService.update(1,attivitaDto)).thenReturn(attivitaDto);
        
        ResultActions response = mockMvc.perform(patch("/api/attivita/1").contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(attivitaDto)));
        
         response.andExpect(MockMvcResultMatchers.status().isOk())
         .andExpect(MockMvcResultMatchers.jsonPath("$.nome", CoreMatchers.is(attivitaDto.getNome())));
         
         verify(attivitaService,times(1)).update(1, attivitaDto);
    }

    @Test
    void attivitaControllerUpdateReturnResourceNotFound() throws Exception {
        
        when(attivitaService.update(1,attivitaDto)).thenThrow(new ResourceNotFoundException("risorsa non trovata"));
        
        ResultActions response = mockMvc.perform(patch("/api/attivita/1").contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(attivitaDto)));
        
         response.andExpect(MockMvcResultMatchers.status().isNotFound())
         .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("risorsa non trovata")))
         .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(404)));

         
         verify(attivitaService,times(1)).update(1, attivitaDto);
    }

    @Test
    void attivitaControllerUpdateReturnDatabaseException() throws Exception {
        
        when(attivitaService.update(1,attivitaDto)).thenThrow(new DatabaseException("conflict"));
        
        ResultActions response = mockMvc.perform(patch("/api/attivita/1").contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(attivitaDto)));
        
         response.andExpect(MockMvcResultMatchers.status().isConflict())
         .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("conflict")))
         .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(409)));

         
         verify(attivitaService,times(1)).update(1, attivitaDto);
    }

    @Test
    void attivitaControllerDeleteReturnOk() throws Exception {
        doNothing().when(attivitaService).delete(1);

        ResultActions response = mockMvc.perform(delete("/api/attivita/1").contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk());

        verify(attivitaService, times(1)).delete(1);
    }

    @Test
    void attivitaControllerDeleteReturnResourceNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("risorsa non trovata!")).when(attivitaService).delete(1);

        ResultActions response = mockMvc.perform(delete("/api/attivita/1").contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(404)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("risorsa non trovata!")));

        verify(attivitaService, times(1)).delete(1);
    }

    @Test
    void attivitaControllerDeleteReturnResou() throws Exception {
        doThrow(new ResourceNotFoundException("risorsa non trovata!")).when(attivitaService).delete(1);

        ResultActions response = mockMvc.perform(delete("/api/attivita/1").contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(404)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("risorsa non trovata!")));

        verify(attivitaService, times(1)).delete(1);
    }

    void attivitaControllerDeleteReturnDatabaseException() throws Exception {
        doThrow(new DatabaseException("conflicts!")).when(attivitaService).delete(1);

        ResultActions response = mockMvc.perform(delete("/api/attivita/1").contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(409)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("conflicts!")));

        verify(attivitaService, times(1)).delete(1);
    }

    
    //DA TESTARE
    void attivitaControllerhandleMethodArgumentNotValid() throws Exception {
        
    }
    
}
