package com.registroformazione.registro;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.registroformazione.dto.RegistroDto;
import com.registroformazione.exceptions.DatabaseException;
import com.registroformazione.exceptions.FilterException;
import com.registroformazione.exceptions.NoDataFoundException;
import com.registroformazione.exceptions.ResourceNotFoundException;
import com.registroformazione.model.Area;
import com.registroformazione.model.Attivita;
import com.registroformazione.model.Cc;
import com.registroformazione.model.Competenza;
import com.registroformazione.model.Persona;
import com.registroformazione.model.Registro;
import com.registroformazione.model.Stato;
import com.registroformazione.model.Vendor;
import com.registroformazione.repository.RegistroRepository;
import com.registroformazione.service.RegistroService;

@ExtendWith(MockitoExtension.class)
 class RegistroServiceTest {

    @InjectMocks
    private RegistroService underTest;

    @Mock
    private RegistroRepository registroRepository;

    @Spy
    private ModelMapper modelMapper;

    private RegistroDto registroDto = new RegistroDto();
    private Registro registro = new Registro();
    private Cc c = new Cc();
    private Area ar = new Area();
    private Competenza co = new Competenza();
    private Vendor v = new Vendor();
    private Persona p = new Persona(1, "Daniele", "Muttoni", true, false, c, null, ar);
    private Stato s = new Stato(1, "Eseguito", null);
    private Attivita at = new Attivita(1, "Aws", "254", co, v, null);

    @BeforeEach
    public void init() {
        registroDto.setAnno(2023);
        registroDto.setAttivitaId(1);
        registroDto.setDataCompletamento(LocalDate.of(2023, 5, 17));
        registroDto.setDataScadenza(LocalDate.of(2024, 5, 17));
        registroDto.setNota(null);
        registroDto.setPersonaId(1);
        registroDto.setStatoId(1);
        registroDto.setTipo("Certificazione");
        registro.setAnno(2023);
        registro.setAttivita(at);
        registro.setDataCompletamento(LocalDate.of(2023, 5, 17));
        registro.setDataScadenza(LocalDate.of(2024, 5, 17));
        registro.setNota(null);
        registro.setPersona(p);
        registro.setStato(s);
        registro.setTipo("Certificazione");
    }

    @Test
    void getAllSuccessfull() {
        PageImpl pageImpl = new PageImpl<>(List.of(registro, registro, registro), PageRequest.of(0, 3), 3);
        given(registroRepository.findAll(any(BooleanExpression.class), any(PageRequest.class))).willReturn(pageImpl);
        Page<RegistroDto> expected = underTest.findAll("tipo:Certificazione", 1, 3);
        verify(registroRepository, times(1)).findAll(any(BooleanExpression.class), any(PageRequest.class));
        assertThat(expected.getContent()).isEqualTo(List.of(registroDto, registroDto, registroDto));
    }

    @Test
    void getAllReturnException() {
        Integer offset = 1, pageSize = 3;
        given(registroRepository.findAll(any(BooleanExpression.class), any(PageRequest.class)))
                .willReturn(new PageImpl<>(Collections.emptyList(), PageRequest.of(offset - 1, pageSize), 0));
        assertThrows(NoDataFoundException.class, () -> underTest.findAll("tipo:Certificazione", offset, pageSize));
    }

    @Test
    void getAllFilterException() {
        Integer offset = 1, pageSize = 3;
        assertThrows(FilterException.class, () -> underTest.findAll("tipo)Certificazione", offset, pageSize));
    }

    @Test
    void getByIdSuccessfull() {
        given(registroRepository.findById(anyInt())).willReturn(Optional.of(registro));
        RegistroDto expected = underTest.findById(1);
        verify(registroRepository, times(1)).findById(1);
        assertThat(expected).isEqualTo(registroDto);
    }

    @Test
    void getByIdException() {
        given(registroRepository.findById(anyInt())).willReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> underTest.findById(1));
        assertEquals("registro non trovato", exception.getMessage());

    }

    @Test
    void createSuccessfull() {
        // when
        when(underTest.convertDtoToEntity(registroDto)).thenReturn(registro);
        RegistroDto expected = underTest.create(registroDto);

        // then
        verify(registroRepository, times(1)).save(registro);
        assertThat(expected).isEqualTo(registroDto);
    }

    @Test
    void createException() {
        when(underTest.convertDtoToEntity(registroDto)).thenReturn(registro);

        given(registroRepository.save(registro)).willThrow(new RuntimeException("constraint error"));

        DatabaseException exception = assertThrows(DatabaseException.class, () -> underTest.create(registroDto));

        assertEquals("inserimento fallito conflitti : constraint error", exception.getMessage());
    }

    @Test
    void updateSuccessfull() {

        when(underTest.convertDtoToEntity(registroDto)).thenReturn(registro);
        given(registroRepository.findById(1)).willReturn(Optional.of(registro));

        // when
        RegistroDto expected = underTest.update(1, registroDto);

        // then
        verify(registroRepository, times(1)).save(registro);
        assertThat(expected).isEqualTo(registroDto);
    }

    @Test
    void updateResourceNotFoundException() {

        given(registroRepository.findById(anyInt())).willReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> underTest.update(1, registroDto));

        assertEquals("registro non trovato", exception.getMessage());

        verify(registroRepository, times(0)).save(registro);

    }

    @Test
    void updateDatabaseException() {
        
        
        when(underTest.convertDtoToEntity(registroDto)).thenReturn(registro);
        given(registroRepository.findById(1)).willReturn(Optional.of(registro));
        given(registroRepository.save(registro)).willThrow(new RuntimeException("conflicts !"));

        //then when
        DatabaseException exception = assertThrows(DatabaseException.class,
                () -> underTest.update(1, registroDto));

        assertEquals("inserimento fallito conflitti : conflicts !", exception.getMessage());
    }

    @Test
    void deleteSuccessful() {
        Integer id = 1;

        Registro foundedRegistro = new Registro();
        foundedRegistro.setTipo("Esame");
        foundedRegistro.setId(1);

        given(registroRepository.findById(1)).willReturn(Optional.of(foundedRegistro));

        underTest.delete(id);
        verify(registroRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteException() {

        Integer id = 1;
        Registro foundedRegistro = new Registro();
        foundedRegistro.setTipo("Esame");
        foundedRegistro.setId(1);

        given(registroRepository.findById(1)).willReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> underTest.delete(id));

        assertEquals("registro non trovato", exception.getMessage());

        verify(registroRepository, times(0)).deleteById(id);
    }

}
