package com.registroformazione.attivita;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.github.javafaker.Faker;
import com.registroformazione.dto.AttivitaDto;
import com.registroformazione.dto.AttivitaDto;
import com.registroformazione.exceptions.DatabaseException;
import com.registroformazione.exceptions.NoDataFoundException;
import com.registroformazione.exceptions.ResourceNotFoundException;
import com.registroformazione.model.Area;
import com.registroformazione.model.Attivita;
import com.registroformazione.model.Competenza;
import com.registroformazione.model.Vendor;
import com.registroformazione.repository.AttivitaRepository;
import com.registroformazione.service.AttivitaService;

@ExtendWith(MockitoExtension.class)
 class AttivitaServiceTest {
    
    @InjectMocks
    private AttivitaService underTest;
    
    @Mock
    private AttivitaRepository attivitaRepository;
    
    @Spy
    private ModelMapper modelMapper;
    
    private AttivitaDto attivitaDto = new AttivitaDto();
    private Attivita attivita = new Attivita();
    private Vendor vendor = new Vendor(2, "Microsoft", null);
    private Competenza competenza = new Competenza(1, "Microsoft foundamentals", null);
    
    
    @BeforeEach
    public void init() {
        
        attivitaDto.setCodice("z45");
        attivitaDto.setNome("azure function 1");
        attivitaDto.setCompetenzaId(1);
        attivitaDto.setVendorId(2);
        
        attivita.setId(null);
        attivita.setCodice("z45");
        attivita.setNome("azure function 1");
        attivita.setCompetenza(competenza);
        attivita.setVendor(vendor);
    }
    
    @Test
    // happy path
    void getAllSuccessfull() {
        // given
        given(attivitaRepository.findAll()).willReturn(List.of(attivita, attivita, attivita));

        // when
        List<AttivitaDto> expected = underTest.findAll();

        // then
        verify(attivitaRepository, times(1)).findAll();
        assertThat(expected).isEqualTo(List.of(attivitaDto, attivitaDto, attivitaDto));
    }

    @Test
    // Exception
    void getAllReturnException() {
        // given
        given(attivitaRepository.findAll()).willReturn(Collections.emptyList());

        // when then
        assertThrows(NoDataFoundException.class, () -> underTest.findAll());
    }
    
    
    @Test
    void getByIdSuccessfull() {
        // given
        given(attivitaRepository.findById(anyInt())).willReturn(Optional.of(attivita));
      

        // when
        AttivitaDto expected = underTest.findById(1);

        // then
        verify(attivitaRepository, times(1)).findById(1);
        assertThat(expected).isEqualTo(attivitaDto);
    }

    @Test
    void getByIdException() {
        // given
        given(attivitaRepository.findById(anyInt())).willReturn(Optional.empty());

        // when then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> underTest.findById(1));

        assertEquals("attivita non trovato", exception.getMessage());

    }

    @Test
    void createSuccessfull() {
        // when
        when(underTest.convertDtoToEntity(attivitaDto)).thenReturn(attivita);
        AttivitaDto expected = underTest.create(attivitaDto);

        // then
        verify(attivitaRepository, times(1)).save(attivita);
        assertThat(expected).isEqualTo(attivitaDto);
    }

    @Test
    void createException() {
        when(underTest.convertDtoToEntity(attivitaDto)).thenReturn(attivita);

        given(attivitaRepository.save(attivita)).willThrow(new RuntimeException("constraint error"));

        DatabaseException exception = assertThrows(DatabaseException.class, () -> underTest.create(attivitaDto));

        assertEquals("inserimento fallito conflitti : constraint error", exception.getMessage());
    }

    @Test
    void updateSuccessfull() {

        when(underTest.convertDtoToEntity(attivitaDto)).thenReturn(attivita);
        given(attivitaRepository.findById(2)).willReturn(Optional.of(attivita));

        // when
        AttivitaDto expected = underTest.update(2, attivitaDto);

        // then
        verify(attivitaRepository, times(1)).save(attivita);
        assertThat(expected).isEqualTo(attivitaDto);
    }

    @Test
    void updateResourceNotFoundException() {

        given(attivitaRepository.findById(anyInt())).willReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> underTest.update(2, attivitaDto));

        assertEquals("attivita non trovato", exception.getMessage());

        verify(attivitaRepository, times(0)).save(attivita);

    }

    @Test
    void updateDatabaseException() {
        
        
        when(underTest.convertDtoToEntity(attivitaDto)).thenReturn(attivita);
        given(attivitaRepository.findById(2)).willReturn(Optional.of(attivita));
        given(attivitaRepository.save(attivita)).willThrow(new RuntimeException("conflicts !"));

        //then when
        DatabaseException exception = assertThrows(DatabaseException.class,
                () -> underTest.update(2, attivitaDto));

        assertEquals("inserimento fallito conflitti : conflicts !", exception.getMessage());
    }
    
    @Test
    void deleteSuccessful() {
        Integer id = 1;
        
        Attivita foundedAttivita = new Attivita();
        foundedAttivita.setNome("a80");
        foundedAttivita.setId(1);

        given(attivitaRepository.findById(1)).willReturn(Optional.of(foundedAttivita));

        underTest.delete(id);
        verify(attivitaRepository,times(1)).deleteById(id);
    }
    
    @Test
    void deleteException() {
        
        Integer id = 1;
        Area foundedAttivita = new Area();
        foundedAttivita.setNome("a80");
        foundedAttivita.setId(1);

        given(attivitaRepository.findById(1)).willReturn(Optional.empty());
        
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> underTest.delete(id));

        assertEquals("Attivita non trovata", exception.getMessage());

        verify(attivitaRepository,times(0)).deleteById(id);
    }
    


}
