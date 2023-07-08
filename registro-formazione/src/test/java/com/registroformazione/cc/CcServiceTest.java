package com.registroformazione.cc;

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
import com.registroformazione.dto.CcDto;
import com.registroformazione.exceptions.DatabaseException;
import com.registroformazione.exceptions.NoDataFoundException;
import com.registroformazione.exceptions.ResourceNotFoundException;
import com.registroformazione.model.Cc;
import com.registroformazione.repository.CcRepository;
import com.registroformazione.service.CcService;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CcServiceTest {
    
    @Mock
    private CcRepository ccRepository;

    @InjectMocks
    private CcService underTest;
    
    @Spy
    private ModelMapper modelMapper;

    @Test
    // happy path
    void getAllSuccessfull() {
        // given
        Faker faker = new Faker();
        Cc cc = new Cc(faker.number().randomDigit(), faker.regexify("[A-Za-z]{3}"), null);
        given(ccRepository.findAll()).willReturn(List.of(cc, cc, cc));
        CcDto ccDto = underTest.convertEntityToDto(cc);

        // when
        List<CcDto> expected = underTest.findAll();

        // then
        verify(ccRepository, times(1)).findAll();
        assertThat(expected).isEqualTo(List.of(ccDto, ccDto, ccDto));
    }

    @Test
    // Exception
    void getAllReturnException() {
        // given
        given(ccRepository.findAll()).willReturn(Collections.emptyList());

        // when then
        assertThrows(NoDataFoundException.class, () -> underTest.findAll());
    }

    @Test
    void getByIdSuccessfull() {
        // given
        Faker faker = new Faker();
        Cc cc = new Cc(faker.number().randomDigit(), faker.regexify("[A-Za-z]{3}"), null);
        given(ccRepository.findById(anyInt())).willReturn(Optional.of(cc));
        CcDto ccDto = underTest.convertEntityToDto(cc);

        // when
        CcDto expected = underTest.findById(1);

        // then
        verify(ccRepository, times(1)).findById(1);
        assertThat(expected).isEqualTo(ccDto);
    }

    @Test
    void getByIdException() {
        // given
        given(ccRepository.findById(anyInt())).willReturn(Optional.empty());

        // when then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> underTest.findById(1));

        assertEquals("cc non trovato", exception.getMessage());

    }

    @Test
    void createSuccessfull() {
        // given
        CcDto ccDto = new CcDto();
        ccDto.setNome("Aws");

        Cc cc = new Cc();
        cc.setId(null);
        cc.setNome("Aws");

        // when
        CcDto expected = underTest.create(ccDto);

        // then
        verify(ccRepository, times(1)).save(cc);
        assertThat(expected).isEqualTo(ccDto);
    }

    @Test
    void createException() {

        CcDto ccDto = new CcDto();
        ccDto.setNome("Aws");

        Cc cc = new Cc();
        cc.setId(null);
        cc.setNome("Aws");

        given(ccRepository.save(cc)).willThrow(new RuntimeException("constraint error"));

        DatabaseException exception = assertThrows(DatabaseException.class, () -> underTest.create(ccDto));

        assertEquals("inserimento fallito conflitti : constraint error", exception.getMessage());
    }

    @Test
    void updateSuccessfull() {
        // given
        CcDto ccDto = new CcDto();
        ccDto.setNome("Scrum");

        Cc cc = new Cc();
        cc.setNome("Scrum");
        cc.setId(2);

        Cc foundedCc = new Cc();
        foundedCc.setNome("Aws");
        foundedCc.setId(2);

        given(ccRepository.findById(2)).willReturn(Optional.of(foundedCc));

        // when
        CcDto expected = underTest.update(2, ccDto);

        // then
        verify(ccRepository, times(1)).save(cc);
        assertThat(expected).isEqualTo(ccDto);
    }

    @Test
    void updateResourceNotFoundException() {
        // given
        CcDto ccDto = new CcDto();
        ccDto.setNome("Scrum");

        Cc cc = new Cc();
        cc.setNome("Scrum");
        cc.setId(2);

        given(ccRepository.findById(anyInt())).willReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> underTest.update(2, ccDto));

        assertEquals("cc non trovato", exception.getMessage());

        verify(ccRepository, times(0)).save(cc);

    }

    @Test
    void updateDatabaseException() {
        // given
        CcDto ccDto = new CcDto();
        ccDto.setNome("Scrum");

        Cc cc = new Cc();
        cc.setNome("Scrum");
        cc.setId(2);

        Cc foundedCc = new Cc();
        foundedCc.setNome("Aws");
        foundedCc.setId(2);

        given(ccRepository.findById(2)).willReturn(Optional.of(foundedCc));
        given(ccRepository.save(cc)).willThrow(new RuntimeException("conflicts !"));

        //then when
        DatabaseException exception = assertThrows(DatabaseException.class,
                () -> underTest.update(2, ccDto));

        assertEquals("inserimento fallito conflitti : conflicts !", exception.getMessage());
    }
    
    @Test
    void deleteSuccessful() {
        Integer id = 1;
        Cc foundedCc = new Cc();
        foundedCc.setNome("Aws");
        foundedCc.setId(1);

        given(ccRepository.findById(1)).willReturn(Optional.of(foundedCc));

        underTest.delete(id);
        verify(ccRepository,times(1)).deleteById(id);
    }
    
    @Test
    void deleteException() {
        Integer id = 1;
        Cc foundedCc = new Cc();
        foundedCc.setNome("Aws");
        foundedCc.setId(1);

        given(ccRepository.findById(1)).willReturn(Optional.empty());
        
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> underTest.delete(id));

        assertEquals("cc non trovato", exception.getMessage());

        verify(ccRepository,times(0)).deleteById(id);
    }
    

}
