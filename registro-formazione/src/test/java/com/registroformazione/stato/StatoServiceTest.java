package com.registroformazione.stato;

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
import com.registroformazione.dto.StatoDto;
import com.registroformazione.exceptions.DatabaseException;
import com.registroformazione.exceptions.NoDataFoundException;
import com.registroformazione.exceptions.ResourceNotFoundException;
import com.registroformazione.model.Stato;
import com.registroformazione.repository.StatoRepository;
import com.registroformazione.service.StatoService;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StatoServiceTest {
    
    @Mock
    private StatoRepository statoRepository;

    @InjectMocks
    private StatoService underTest;
    
    @Spy
    private ModelMapper modelMapper;

    @Test
    // happy path
    void getAllSuccessfull() {
        // given
        Faker faker = new Faker();
        Stato stato = new Stato(faker.number().randomDigit(), faker.regexify("[A-Za-z]{3}"), null);
        given(statoRepository.findAll()).willReturn(List.of(stato, stato, stato));
        StatoDto statoDto = underTest.convertEntityToDto(stato);

        // when
        List<StatoDto> expected = underTest.findAll();

        // then
        verify(statoRepository, times(1)).findAll();
        assertThat(expected).isEqualTo(List.of(statoDto, statoDto, statoDto));
    }

    @Test
    // Exception
    void getAllReturnException() {
        // given
        given(statoRepository.findAll()).willReturn(Collections.emptyList());

        // when then
        assertThrows(NoDataFoundException.class, () -> underTest.findAll());
    }

    @Test
    void getByIdSuccessfull() {
        // given
        Faker faker = new Faker();
        Stato stato = new Stato(faker.number().randomDigit(), faker.regexify("[A-Za-z]{3}"), null);
        given(statoRepository.findById(anyInt())).willReturn(Optional.of(stato));
        StatoDto statoDto = underTest.convertEntityToDto(stato);

        // when
        StatoDto expected = underTest.findById(1);

        // then
        verify(statoRepository, times(1)).findById(1);
        assertThat(expected).isEqualTo(statoDto);
    }

    @Test
    void getByIdException() {
        // given
        given(statoRepository.findById(anyInt())).willReturn(Optional.empty());

        // when then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> underTest.findById(1));

        assertEquals("Stato non trovato", exception.getMessage());

    }

    @Test
    void createSuccessfull() {
        // given
        StatoDto statoDto = new StatoDto();
        statoDto.setNome("Eseguito");

        Stato stato = new Stato();
        stato.setId(null);
        stato.setNome("Eseguito");

        // when
        StatoDto expected = underTest.create(statoDto);

        // then
        verify(statoRepository, times(1)).save(stato);
        assertThat(expected).isEqualTo(statoDto);
    }

    @Test
    void createException() {

        StatoDto statoDto = new StatoDto();
        statoDto.setNome("Eseguito");

        Stato stato = new Stato();
        stato.setId(null);
        stato.setNome("Eseguito");

        given(statoRepository.save(stato)).willThrow(new RuntimeException("constraint error"));

        DatabaseException exception = assertThrows(DatabaseException.class, () -> underTest.create(statoDto));

        assertEquals("inserimento fallito conflitti : constraint error", exception.getMessage());
    }

    @Test
    void updateSuccessfull() {
        // given
        StatoDto statoDto = new StatoDto();
        statoDto.setNome("Pianificato");

        Stato stato = new Stato();
        stato.setNome("Pianificato");
        stato.setId(2);

        Stato foundedStato = new Stato();
        foundedStato.setNome("Eseguito");
        foundedStato.setId(2);

        given(statoRepository.findById(2)).willReturn(Optional.of(foundedStato));

        // when
        StatoDto expected = underTest.update(2, statoDto);

        // then
        verify(statoRepository, times(1)).save(stato);
        assertThat(expected).isEqualTo(statoDto);
    }

    @Test
    void updateResourceNotFoundException() {
        // given
        StatoDto statoDto = new StatoDto();
        statoDto.setNome("Pianificato");

        Stato stato = new Stato();
        stato.setNome("Pianificato");
        stato.setId(2);

        given(statoRepository.findById(anyInt())).willReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> underTest.update(2, statoDto));

        assertEquals("Stato non trovato", exception.getMessage());

        verify(statoRepository, times(0)).save(stato);

    }

    @Test
    void updateDatabaseException() {
        // given
        StatoDto statoDto = new StatoDto();
        statoDto.setNome("Pianificato");

        Stato stato = new Stato();
        stato.setNome("Pianificato");
        stato.setId(2);

        Stato foundedStato = new Stato();
        foundedStato.setNome("Eseguito");
        foundedStato.setId(2);

        given(statoRepository.findById(2)).willReturn(Optional.of(foundedStato));
        given(statoRepository.save(stato)).willThrow(new RuntimeException("conflicts !"));

        //then when
        DatabaseException exception = assertThrows(DatabaseException.class,
                () -> underTest.update(2, statoDto));

        assertEquals("inserimento fallito conflitti : conflicts !", exception.getMessage());
    }
    
    @Test
    void deleteSuccessful() {
        Integer id = 1;
        Stato foundedStato = new Stato();
        foundedStato.setNome("Eseguito");
        foundedStato.setId(1);

        given(statoRepository.findById(1)).willReturn(Optional.of(foundedStato));

        underTest.delete(id);
        verify(statoRepository,times(1)).deleteById(id);
    }
    
    @Test
    void deleteException() {
        Integer id = 1;
        Stato foundedStato = new Stato();
        foundedStato.setNome("Eseguito");
        foundedStato.setId(1);

        given(statoRepository.findById(1)).willReturn(Optional.empty());
        
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> underTest.delete(id));

        assertEquals("Stato non trovato", exception.getMessage());

        verify(statoRepository,times(0)).deleteById(id);
    }
    

}
