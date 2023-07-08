package com.registroformazione.area;

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
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.registroformazione.controllers.AreaController;
import com.registroformazione.dto.AreaDto;
import com.registroformazione.exceptions.DatabaseException;
import com.registroformazione.exceptions.NoDataFoundException;
import com.registroformazione.exceptions.ResourceNotFoundException;
import com.registroformazione.model.Area;
import com.registroformazione.security.config.JwtAuthenticationFilter;
import com.registroformazione.service.AreaService;

@WebMvcTest(controllers = AreaController.class)
@AutoConfigureMockMvc(addFilters = false)//disabilito i filtri di sicurezza sui controller
@ExtendWith(MockitoExtension.class)
class AreaControllerTest {
    

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AreaService areaService;
	
	@Autowired
	private ObjectMapper objectMapper;
	
    @MockBean 
	private JwtAuthenticationFilter authFilter;
    
    @MockBean
    private DateTimeProvider provider;
   
	

	private Area area = new Area();
	private AreaDto areaDto = new AreaDto();

	@BeforeEach
	public void init() {
		areaDto.setNome("A80");

		area.setId(1);
		area.setNome("A80");
	}

	@Test
	void areaControllerFindAllReturnList() throws Exception {

		List<AreaDto> returnedList = new ArrayList<>();
		returnedList.add(areaDto);
		returnedList.add(areaDto);
		returnedList.add(areaDto);

		when(areaService.findAll()).thenReturn(returnedList);

		mockMvc.perform(get("/api/aree").contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.jsonPath("$").isArray()).andExpect(jsonPath("$.length()").value(3));
		verify(areaService, times(1)).findAll();
	}

	@Test
	void areaControllerFindAllReturnNoContent() throws Exception {
		
		when(areaService.findAll()).thenThrow(new NoDataFoundException());
		
		mockMvc.perform(get("/api/aree").contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers.status().isNoContent());
		verify(areaService,times(1)).findAll();
	}

	@Test
	void areaControllerFindByIdReturnOk() throws Exception {
		
		when(areaService.findById(1)).thenReturn(areaDto);
		
		ResultActions response = mockMvc.perform(get("/api/aree/1").contentType(MediaType.APPLICATION_JSON));
		
		 response.andExpect(MockMvcResultMatchers.status().isOk())
         .andExpect(MockMvcResultMatchers.jsonPath("$.nome", CoreMatchers.is(areaDto.getNome())));
		 verify(areaService,times(1)).findById(1);
	}

	@Test
	void areaControllerFindByIdReturnResourceNotFound() throws Exception {
		
		when(areaService.findById(1)).thenThrow(new ResourceNotFoundException("risorsa non trovata"));
		
		ResultActions response = mockMvc.perform(get("/api/aree/1").contentType(MediaType.APPLICATION_JSON));
		
		 response.andExpect(MockMvcResultMatchers.status().isNotFound())
         .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(404)))
         .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("risorsa non trovata")));;

		 verify(areaService,times(1)).findById(1);
	}

	@Test
	void areaControllerCreateReturnCreated() throws Exception {
		
		when(areaService.create(areaDto)).thenReturn(areaDto);
		
		ResultActions response = mockMvc.perform(post("/api/aree").contentType(MediaType.APPLICATION_JSON)
				 .content(objectMapper.writeValueAsString(areaDto)));
		
		 response.andExpect(MockMvcResultMatchers.status().isCreated())
         .andExpect(MockMvcResultMatchers.jsonPath("$.nome", CoreMatchers.is(areaDto.getNome())));

		 verify(areaService,times(1)).create(areaDto);
	}

	@Test
	void areaControllerCreateReturnConflicts() throws Exception {
		
		when(areaService.create(areaDto)).thenThrow(new DatabaseException("errore conflitti!"));
		
		ResultActions response = mockMvc.perform(post("/api/aree").contentType(MediaType.APPLICATION_JSON)
				 .content(objectMapper.writeValueAsString(areaDto)));
		
		 response.andExpect(MockMvcResultMatchers.status().isConflict())
         .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(409)))
         .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("errore conflitti!")));

		 verify(areaService,times(1)).create(areaDto);
	}

	@Test
	void areaControllerUpdateReturnOk() throws Exception {
		
		when(areaService.update(1,areaDto)).thenReturn(areaDto);
		
		ResultActions response = mockMvc.perform(patch("/api/aree/1").contentType(MediaType.APPLICATION_JSON)
				 .content(objectMapper.writeValueAsString(areaDto)));
		
		 response.andExpect(MockMvcResultMatchers.status().isOk())
         .andExpect(MockMvcResultMatchers.jsonPath("$.nome", CoreMatchers.is(areaDto.getNome())));
		 
		 verify(areaService,times(1)).update(1, areaDto);
	}

	@Test
	void areaControllerUpdateReturnResourceNotFound() throws Exception {
		
		when(areaService.update(1,areaDto)).thenThrow(new ResourceNotFoundException("risorsa non trovata"));
		
		ResultActions response = mockMvc.perform(patch("/api/aree/1").contentType(MediaType.APPLICATION_JSON)
				 .content(objectMapper.writeValueAsString(areaDto)));
		
		 response.andExpect(MockMvcResultMatchers.status().isNotFound())
         .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("risorsa non trovata")))
         .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(404)));

		 
		 verify(areaService,times(1)).update(1, areaDto);
	}

	@Test
	void areaControllerUpdateReturnDatabaseException() throws Exception {
		
		when(areaService.update(1,areaDto)).thenThrow(new DatabaseException("conflict"));
		
		ResultActions response = mockMvc.perform(patch("/api/aree/1").contentType(MediaType.APPLICATION_JSON)
				 .content(objectMapper.writeValueAsString(areaDto)));
		
		 response.andExpect(MockMvcResultMatchers.status().isConflict())
         .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("conflict")))
         .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(409)));

		 
		 verify(areaService,times(1)).update(1, areaDto);
	}

	@Test
	void areaControllerDeleteReturnOk() throws Exception {
		doNothing().when(areaService).delete(1);

		ResultActions response = mockMvc.perform(delete("/api/aree/1").contentType(MediaType.APPLICATION_JSON));

		response.andExpect(MockMvcResultMatchers.status().isOk());

		verify(areaService, times(1)).delete(1);
	}

	@Test
	void areaControllerDeleteReturnResourceNotFound() throws Exception {
		doThrow(new ResourceNotFoundException("risorsa non trovata!")).when(areaService).delete(1);

		ResultActions response = mockMvc.perform(delete("/api/aree/1").contentType(MediaType.APPLICATION_JSON));

		response.andExpect(MockMvcResultMatchers.status().isNotFound())
				.andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(404)))
				.andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("risorsa non trovata!")));

		verify(areaService, times(1)).delete(1);
	}

	@Test
	void areaControllerDeleteReturnResou() throws Exception {
		doThrow(new ResourceNotFoundException("risorsa non trovata!")).when(areaService).delete(1);

		ResultActions response = mockMvc.perform(delete("/api/aree/1").contentType(MediaType.APPLICATION_JSON));

		response.andExpect(MockMvcResultMatchers.status().isNotFound())
				.andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(404)))
				.andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("risorsa non trovata!")));

		verify(areaService, times(1)).delete(1);
	}

	void areaControllerDeleteReturnDatabaseException() throws Exception {
		doThrow(new DatabaseException("conflicts!")).when(areaService).delete(1);

		ResultActions response = mockMvc.perform(delete("/api/aree/1").contentType(MediaType.APPLICATION_JSON));

		response.andExpect(MockMvcResultMatchers.status().isConflict())
				.andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", CoreMatchers.is(409)))
				.andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("conflicts!")));

		verify(areaService, times(1)).delete(1);
	}

	
	//DA TESTARE
	void areaControllerhandleMethodArgumentNotValid() throws Exception {
		
	}

}
