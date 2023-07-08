package com.registroformazione.vendor;

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
import com.registroformazione.dto.VendorDto;
import com.registroformazione.exceptions.DatabaseException;
import com.registroformazione.exceptions.NoDataFoundException;
import com.registroformazione.exceptions.ResourceNotFoundException;
import com.registroformazione.model.Vendor;
import com.registroformazione.repository.VendorRepository;
import com.registroformazione.service.VendorService;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class VendorServiceTest {
    
    @Mock
    private VendorRepository vendorRepository;

    @InjectMocks
    private VendorService underTest;
    
    @Spy
    private ModelMapper modelMapper;

    @Test
    // happy path
    void getAllSuccessfull() {
        // given
        Faker faker = new Faker();
        Vendor vendor = new Vendor(faker.number().randomDigit(), faker.regexify("[A-Za-z]{3}"), null);
        given(vendorRepository.findAll()).willReturn(List.of(vendor, vendor, vendor));
        VendorDto vendorDto = underTest.convertEntityToDto(vendor);

        // when
        List<VendorDto> expected = underTest.findAll();

        // then
        verify(vendorRepository, times(1)).findAll();
        assertThat(expected).isEqualTo(List.of(vendorDto, vendorDto, vendorDto));
    }

    @Test
    // Exception
    void getAllReturnException() {
        // given
        given(vendorRepository.findAll()).willReturn(Collections.emptyList());

        // when then
        assertThrows(NoDataFoundException.class, () -> underTest.findAll());
    }

    @Test
    void getByIdSuccessfull() {
        // given
        Faker faker = new Faker();
        Vendor vendor = new Vendor(faker.number().randomDigit(), faker.regexify("[A-Za-z]{3}"), null);
        given(vendorRepository.findById(anyInt())).willReturn(Optional.of(vendor));
        VendorDto vendorDto = underTest.convertEntityToDto(vendor);

        // when
        VendorDto expected = underTest.findById(1);

        // then
        verify(vendorRepository, times(1)).findById(1);
        assertThat(expected).isEqualTo(vendorDto);
    }

    @Test
    void getByIdException() {
        // given
        given(vendorRepository.findById(anyInt())).willReturn(Optional.empty());

        // when then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> underTest.findById(1));

        assertEquals("Vendor non trovato", exception.getMessage());

    }

    @Test
    void createSuccessfull() {
        // given
        VendorDto vendorDto = new VendorDto();
        vendorDto.setNome("Amazon");

        Vendor vendor = new Vendor();
        vendor.setId(null);
        vendor.setNome("Amazon");

        // when
        VendorDto expected = underTest.create(vendorDto);

        // then
        verify(vendorRepository, times(1)).save(vendor);
        assertThat(expected).isEqualTo(vendorDto);
    }

    @Test
    void createException() {

        VendorDto vendorDto = new VendorDto();
        vendorDto.setNome("Amazon");

        Vendor vendor = new Vendor();
        vendor.setId(null);
        vendor.setNome("Amazon");

        given(vendorRepository.save(vendor)).willThrow(new RuntimeException("constraint error"));

        DatabaseException exception = assertThrows(DatabaseException.class, () -> underTest.create(vendorDto));

        assertEquals("inserimento fallito conflitti : constraint error", exception.getMessage());
    }

    @Test
    void updateSuccessfull() {
        // given
        VendorDto vendorDto = new VendorDto();
        vendorDto.setNome("Microsoft");

        Vendor vendor = new Vendor();
        vendor.setNome("Microsoft");
        vendor.setId(2);

        Vendor foundedVendor = new Vendor();
        foundedVendor.setNome("Amazon");
        foundedVendor.setId(2);

        given(vendorRepository.findById(2)).willReturn(Optional.of(foundedVendor));

        // when
        VendorDto expected = underTest.update(2, vendorDto);

        // then
        verify(vendorRepository, times(1)).save(vendor);
        assertThat(expected).isEqualTo(vendorDto);
    }

    @Test
    void updateResourceNotFoundException() {
        // given
        VendorDto vendorDto = new VendorDto();
        vendorDto.setNome("Microsoft");

        Vendor vendor = new Vendor();
        vendor.setNome("Microsoft");
        vendor.setId(2);

        given(vendorRepository.findById(anyInt())).willReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> underTest.update(2, vendorDto));

        assertEquals("Vendor non trovato", exception.getMessage());

        verify(vendorRepository, times(0)).save(vendor);

    }

    @Test
    void updateDatabaseException() {
        // given
        VendorDto vendorDto = new VendorDto();
        vendorDto.setNome("Microsoft");

        Vendor vendor = new Vendor();
        vendor.setNome("Microsoft");
        vendor.setId(2);

        Vendor foundedVendor = new Vendor();
        foundedVendor.setNome("Amazon");
        foundedVendor.setId(2);

        given(vendorRepository.findById(2)).willReturn(Optional.of(foundedVendor));
        given(vendorRepository.save(vendor)).willThrow(new RuntimeException("conflicts !"));

        //then when
        DatabaseException exception = assertThrows(DatabaseException.class,
                () -> underTest.update(2, vendorDto));

        assertEquals("inserimento fallito conflitti : conflicts !", exception.getMessage());
    }
    
    @Test
    void deleteSuccessful() {
        Integer id = 1;
        Vendor foundedVendor = new Vendor();
        foundedVendor.setNome("Amazon");
        foundedVendor.setId(1);

        given(vendorRepository.findById(1)).willReturn(Optional.of(foundedVendor));

        underTest.delete(id);
        verify(vendorRepository,times(1)).deleteById(id);
    }
    
    @Test
    void deleteException() {
        Integer id = 1;
        Vendor foundedVendor = new Vendor();
        foundedVendor.setNome("Amazon");
        foundedVendor.setId(1);

        given(vendorRepository.findById(1)).willReturn(Optional.empty());
        
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> underTest.delete(id));

        assertEquals("Vendor non trovato", exception.getMessage());

        verify(vendorRepository,times(0)).deleteById(id);
    }
    

}
