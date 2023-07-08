package com.registroformazione.competenza;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;


import com.github.javafaker.Faker;
import com.registroformazione.dto.CompetenzaDto;
import com.registroformazione.exceptions.DatabaseException;
import com.registroformazione.exceptions.NoDataFoundException;
import com.registroformazione.exceptions.ResourceNotFoundException;
import com.registroformazione.model.Competenza;
import com.registroformazione.repository.CompetenzaRepository;
import com.registroformazione.service.CompetenzaService;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CompetenzaServiceTest {
    
    @Mock
    private CompetenzaRepository competenzaRepository;

    @InjectMocks
    private CompetenzaService underTest;
    
    @Spy
    private ModelMapper modelMapper;

    @Test
    // happy path
    void getAllSuccessfull() {
        // given
        Faker faker = new Faker();
        Competenza competenza = new Competenza(faker.number().randomDigit(), faker.regexify("[A-Za-z]{3}"), null);
        given(competenzaRepository.findAll()).willReturn(List.of(competenza, competenza, competenza));
        CompetenzaDto competenzaDto = underTest.convertEntityToDto(competenza);

        // when
        List<CompetenzaDto> expected = underTest.findAll();

        // then
        verify(competenzaRepository, times(1)).findAll();
        assertThat(expected).isEqualTo(List.of(competenzaDto, competenzaDto, competenzaDto));
    }

    @Test
    // Exception
    void getAllReturnException() {
        // given
        given(competenzaRepository.findAll()).willReturn(Collections.emptyList());

        // when then
        assertThrows(NoDataFoundException.class, () -> underTest.findAll());
    }

    @Test
    void getByIdSuccessfull() {
        // given
        Faker faker = new Faker();
        Competenza competenza = new Competenza(faker.number().randomDigit(), faker.regexify("[A-Za-z]{3}"), null);
        given(competenzaRepository.findById(anyInt())).willReturn(Optional.of(competenza));
        CompetenzaDto competenzaDto = underTest.convertEntityToDto(competenza);

        // when
        CompetenzaDto expected = underTest.findById(1);

        // then
        verify(competenzaRepository, times(1)).findById(1);
        assertThat(expected).isEqualTo(competenzaDto);
    }

    @Test
    void getByIdException() {
        // given
        given(competenzaRepository.findById(anyInt())).willReturn(Optional.empty());

        // when then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> underTest.findById(1));

        assertEquals("Competenza non trovata", exception.getMessage());

    }

    @Test
    void createSuccessfull() {
        // given
        CompetenzaDto competenzaDto = new CompetenzaDto();
        competenzaDto.setNome("Servizi Aws");

        Competenza competenza = new Competenza();
        competenza.setId(null);
        competenza.setNome("Servizi Aws");

        // when
        CompetenzaDto expected = underTest.create(competenzaDto);

        // then
        verify(competenzaRepository, times(1)).save(competenza);
        assertThat(expected).isEqualTo(competenzaDto);
    }

    @Test
    void createException() {

        CompetenzaDto competenzaDto = new CompetenzaDto();
        competenzaDto.setNome("Servizi Aws");

        Competenza competenza = new Competenza();
        competenza.setId(null);
        competenza.setNome("Servizi Aws");

        given(competenzaRepository.save(competenza)).willThrow(new RuntimeException("constraint error"));

        DatabaseException exception = assertThrows(DatabaseException.class, () -> underTest.create(competenzaDto));

        assertEquals("inserimento fallito conflitti : constraint error", exception.getMessage());
    }

    @Test
    void updateSuccessfull() {
        // given
        CompetenzaDto competenzaDto = new CompetenzaDto();
        competenzaDto.setNome("Cloud Security");

        Competenza competenza = new Competenza();
        competenza.setNome("Cloud Security");
        competenza.setId(2);

        Competenza foundedCompetenza = new Competenza();
        foundedCompetenza.setNome("Servizi Aws");
        foundedCompetenza.setId(2);

        given(competenzaRepository.findById(2)).willReturn(Optional.of(foundedCompetenza));

        // when
        CompetenzaDto expected = underTest.update(2, competenzaDto);

        // then
        verify(competenzaRepository, times(1)).save(competenza);
        assertThat(expected).isEqualTo(competenzaDto);
    }

    @Test
    void updateResourceNotFoundException() {
        // given
        CompetenzaDto competenzaDto = new CompetenzaDto();
        competenzaDto.setNome("Cloud Security");

        Competenza competenza = new Competenza();
        competenza.setNome("Cloud Security");
        competenza.setId(2);

        given(competenzaRepository.findById(anyInt())).willReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> underTest.update(2, competenzaDto));

        assertEquals("Competenza non trovata", exception.getMessage());

        verify(competenzaRepository, times(0)).save(competenza);

    }

    @Test
    void updateDatabaseException() {
        // given
        CompetenzaDto competenzaDto = new CompetenzaDto();
        competenzaDto.setNome("Cloud Security");

        Competenza competenza = new Competenza();
        competenza.setNome("Cloud Security");
        competenza.setId(2);

        Competenza foundedCompetenza = new Competenza();
        foundedCompetenza.setNome("Servizi Aws");
        foundedCompetenza.setId(2);

        given(competenzaRepository.findById(2)).willReturn(Optional.of(foundedCompetenza));
        given(competenzaRepository.save(competenza)).willThrow(new RuntimeException("conflicts !"));

        //then when
        DatabaseException exception = assertThrows(DatabaseException.class,
                () -> underTest.update(2, competenzaDto));

        assertEquals("inserimento fallito conflitti : conflicts !", exception.getMessage());
    }
    
    @Test
    void deleteSuccessful() {
        Integer id = 1;
        Competenza foundedCompetenza = new Competenza();
        foundedCompetenza.setNome("Servizi Aws");
        foundedCompetenza.setId(1);

        given(competenzaRepository.findById(1)).willReturn(Optional.of(foundedCompetenza));

        underTest.delete(id);
        verify(competenzaRepository,times(1)).deleteById(id);
    }
    
    @Test
    void deleteException() {
        Integer id = 1;
        Competenza foundedCompetenza = new Competenza();
        foundedCompetenza.setNome("Servizi Aws");
        foundedCompetenza.setId(1);

        given(competenzaRepository.findById(1)).willReturn(Optional.empty());
        
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> underTest.delete(id));

        assertEquals("Competenza non trovata", exception.getMessage());

        verify(competenzaRepository,times(0)).deleteById(id);
    }
    

}
