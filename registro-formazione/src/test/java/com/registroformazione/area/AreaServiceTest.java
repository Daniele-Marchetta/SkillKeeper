package com.registroformazione.area;

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
import com.registroformazione.dto.AreaDto;
import com.registroformazione.exceptions.DatabaseException;
import com.registroformazione.exceptions.NoDataFoundException;
import com.registroformazione.exceptions.ResourceNotFoundException;
import com.registroformazione.model.Area;
import com.registroformazione.repository.AreaRepository;
import com.registroformazione.service.AreaService;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AreaServiceTest {
	
    @Mock
	private AreaRepository areaRepository;

	@InjectMocks
	private AreaService underTest;
	
	@Spy
	private ModelMapper modelMapper;

	@Test
	// happy path
	void getAllSuccessfull() {
		// given
		Faker faker = new Faker();
		Area area = new Area(faker.number().randomDigit(), faker.regexify("[A-Za-z]{3}"));
		given(areaRepository.findAll()).willReturn(List.of(area, area, area));
		AreaDto areaDto = underTest.convertEntityToDto(area);

		// when
		List<AreaDto> expected = underTest.findAll();

		// then
		verify(areaRepository, times(1)).findAll();
		assertThat(expected).isEqualTo(List.of(areaDto, areaDto, areaDto));
	}

	@Test
	// Exception
	void getAllReturnException() {
		// given
		given(areaRepository.findAll()).willReturn(Collections.emptyList());

		// when then
		assertThrows(NoDataFoundException.class, () -> underTest.findAll());
	}

	@Test
	void getByIdSuccessfull() {
		// given
		Faker faker = new Faker();
		Area area = new Area(faker.number().randomDigit(), faker.regexify("[A-Za-z]{3}"));
		given(areaRepository.findById(anyInt())).willReturn(Optional.of(area));
		AreaDto areaDto = underTest.convertEntityToDto(area);

		// when
		AreaDto expected = underTest.findById(1);

		// then
		verify(areaRepository, times(1)).findById(1);
		assertThat(expected).isEqualTo(areaDto);
	}

	@Test
	void getByIdException() {
		// given
		given(areaRepository.findById(anyInt())).willReturn(Optional.empty());

		// when then
		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
				() -> underTest.findById(1));

		assertEquals("area non trovata", exception.getMessage());

	}

	@Test
	void createSuccessfull() {
		// given
		AreaDto areaDto = new AreaDto();
		areaDto.setNome("a80");

		Area area = new Area();
		area.setId(null);
		area.setNome("a80");

		// when
		AreaDto expected = underTest.create(areaDto);

		// then
		verify(areaRepository, times(1)).save(area);
		assertThat(expected).isEqualTo(areaDto);
	}

	@Test
	void createException() {

		AreaDto areaDto = new AreaDto();
		areaDto.setNome("a80");

		Area area = new Area();
		area.setId(null);
		area.setNome("a80");

		given(areaRepository.save(area)).willThrow(new RuntimeException("constraint error"));

		DatabaseException exception = assertThrows(DatabaseException.class, () -> underTest.create(areaDto));

		assertEquals("inserimento fallito conflitti : constraint error", exception.getMessage());
	}

	@Test
	void updateSuccessfull() {
		// given
		AreaDto areaDto = new AreaDto();
		areaDto.setNome("a90");

		Area area = new Area();
		area.setNome("a90");
		area.setId(2);

		Area foundedArea = new Area();
		foundedArea.setNome("a80");
		foundedArea.setId(2);

		given(areaRepository.findById(2)).willReturn(Optional.of(foundedArea));

		// when
		AreaDto expected = underTest.update(2, areaDto);

		// then
		verify(areaRepository, times(1)).save(area);
		assertThat(expected).isEqualTo(areaDto);
	}

	@Test
	void updateResourceNotFoundException() {
		// given
		AreaDto areaDto = new AreaDto();
		areaDto.setNome("a90");

		Area area = new Area();
		area.setNome("a90");
		area.setId(2);

		given(areaRepository.findById(anyInt())).willReturn(Optional.empty());

		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
				() -> underTest.update(2, areaDto));

		assertEquals("area non trovata", exception.getMessage());

		verify(areaRepository, times(0)).save(area);

	}

	@Test
	void updateDatabaseException() {
		// given
		AreaDto areaDto = new AreaDto();
		areaDto.setNome("a90");

		Area area = new Area();
		area.setNome("a90");
		area.setId(2);

		Area foundedArea = new Area();
		foundedArea.setNome("a80");
		foundedArea.setId(2);

		given(areaRepository.findById(2)).willReturn(Optional.of(foundedArea));
		given(areaRepository.save(area)).willThrow(new RuntimeException("conflicts !"));

        //then when
		DatabaseException exception = assertThrows(DatabaseException.class,
				() -> underTest.update(2, areaDto));

		assertEquals("inserimento fallito conflitti : conflicts !", exception.getMessage());
	}
	
	@Test
	void deleteSuccessful() {
		Integer id = 1;
		Area foundedArea = new Area();
		foundedArea.setNome("a80");
		foundedArea.setId(1);

		given(areaRepository.findById(1)).willReturn(Optional.of(foundedArea));

		underTest.delete(id);
		verify(areaRepository,times(1)).deleteById(id);
	}
	
	@Test
	void deleteException() {
		Integer id = 1;
		Area foundedArea = new Area();
		foundedArea.setNome("a80");
		foundedArea.setId(1);

		given(areaRepository.findById(1)).willReturn(Optional.empty());
		
		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
				() -> underTest.delete(id));

		assertEquals("Area non trovata", exception.getMessage());

		verify(areaRepository,times(0)).deleteById(id);
	}
	

}
