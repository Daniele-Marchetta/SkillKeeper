package com.registroformazione.persone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.BooleanOperation;
import com.registroformazione.dto.PersonaDto;
import com.registroformazione.dto.PersonaDto;
import com.registroformazione.exceptions.DatabaseException;
import com.registroformazione.exceptions.FilterException;
import com.registroformazione.exceptions.NoDataFoundException;
import com.registroformazione.exceptions.ResourceNotFoundException;
import com.registroformazione.model.Area;
import com.registroformazione.model.Cc;
import com.registroformazione.model.Persona;
import com.registroformazione.repository.PersonaRepository;
import com.registroformazione.service.PersonaService;



@ExtendWith(MockitoExtension.class)
 class PersonaServiceTest {
    
    @Spy
    private PersonaRepository personaRepository;

    @InjectMocks
    private PersonaService underTest;
    
    @Spy
    private ModelMapper modelMapper;
    
    private Persona persona = new Persona();
    private PersonaDto personaDto = new PersonaDto();
    private Area area = new Area(1,"a80");
    private Cc cc = new Cc(2,"Java",null);

    
    
    @BeforeEach
    public void init() {
        persona.setNome("Daniele");
        persona.setCognome("Rossi");
        persona.setDeleted(false);
        persona.setInForza(false);
        persona.setArea(area);
        persona.setCc(cc);
        
        personaDto.setCognome("Rossi");
        personaDto.setNome("Daniele");
        personaDto.setInForza(false);
        personaDto.setAreaId(1);
        personaDto.setCcId(2);
}
    
    
    @Test
    // happy path
    void getAllSuccessfull() {
        
        // given
        Integer offset=1;
        Integer pageSize=10;
        PageImpl pageImpl=new PageImpl<>(List.of(persona, persona, persona),  PageRequest.of(0, 3), 3);

        given(personaRepository.findAll(any(BooleanExpression.class),any(PageRequest.class))).willReturn(pageImpl);
        // when

        // then
        Page<PersonaDto> expected = underTest.findAll("nome:Daniele",offset,pageSize);
        
        verify(personaRepository, times(1)).findAll(any(BooleanExpression.class),any(PageRequest.class));


    }
    
    @Test
    // Exception
    void getAllReturnNoDataFoundException() {
        
        Integer offset=1;
        Integer pageSize=6;
        // given
        given(personaRepository.findAll(any(BooleanExpression.class),any(PageRequest.class))).willReturn(new PageImpl<>(Collections.emptyList(),  PageRequest.of(offset-1, pageSize), 0));

        // when then
        assertThrows(NoDataFoundException.class, () -> underTest.findAll("nome:Alberto",offset,pageSize));
    }
    
   @Test
   void getAllReturnFilterException() {
        
        Integer offset=1;
        Integer pageSize=6;

        assertThrows(FilterException.class, () -> underTest.findAll("nome/Alberto",offset,pageSize));
    }
   
   
   @Test
   void getByIdSuccessfull() {
       // given
       given(personaRepository.findById(anyInt())).willReturn(Optional.of(persona));
     

       // when
       PersonaDto expected = underTest.findById(1);

       // then
       verify(personaRepository, times(1)).findById(1);
       assertThat(expected).isEqualTo(personaDto);
   }

   @Test
   void getByIdException() {
       // given
       given(personaRepository.findById(anyInt())).willReturn(Optional.empty());

       // when then
       ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
               () -> underTest.findById(1));

       assertEquals("Persona non trovata", exception.getMessage());

   }

   @Test
   void createSuccessfull() {
       // when
       when(underTest.convertDtoToEntity(personaDto)).thenReturn(persona);
       PersonaDto expected = underTest.create(personaDto);

       // then
       verify(personaRepository, times(1)).save(persona);
       assertThat(expected).isEqualTo(personaDto);
   }

   @Test
   void createException() {
       when(underTest.convertDtoToEntity(personaDto)).thenReturn(persona);

       given(personaRepository.save(persona)).willThrow(new RuntimeException("constraint error"));

       DatabaseException exception = assertThrows(DatabaseException.class, () -> underTest.create(personaDto));

       assertEquals("inserimento fallito conflitti : constraint error", exception.getMessage());
   }

   @Test
   void updateSuccessfull() {

       when(underTest.convertDtoToEntity(personaDto)).thenReturn(persona);
       given(personaRepository.findById(2)).willReturn(Optional.of(persona));

       // when
       PersonaDto expected = underTest.update(2, personaDto);

       // then
       verify(personaRepository, times(1)).save(persona);
       assertThat(expected).isEqualTo(personaDto);
   }

   @Test
   void updateResourceNotFoundException() {

       given(personaRepository.findById(anyInt())).willReturn(Optional.empty());

       ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
               () -> underTest.update(2, personaDto));

       assertEquals("Persona non trovata", exception.getMessage());

       verify(personaRepository, times(0)).save(persona);

   }

   @Test
   void updateDatabaseException() {
       
       
       when(underTest.convertDtoToEntity(personaDto)).thenReturn(persona);
       given(personaRepository.findById(2)).willReturn(Optional.of(persona));
       given(personaRepository.save(persona)).willThrow(new RuntimeException("conflicts !"));

       //then when
       DatabaseException exception = assertThrows(DatabaseException.class,
               () -> underTest.update(2, personaDto));

       assertEquals("inserimento fallito conflitti : conflicts !", exception.getMessage());
   }
   
   @Test
   void deleteSuccessful() {
       Integer id = 1;

       given(personaRepository.findById(1)).willReturn(Optional.of(persona));

       underTest.delete(id);
       verify(personaRepository,times(1)).deleteById(id);
   }
   
   @Test
   void deleteException() {
       
       Integer id = 1;

       given(personaRepository.findById(1)).willReturn(Optional.empty());
       
       ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
               () -> underTest.delete(id));

       assertEquals("Persona non trovata", exception.getMessage());

       verify(personaRepository,times(0)).deleteById(id);
   }
}
