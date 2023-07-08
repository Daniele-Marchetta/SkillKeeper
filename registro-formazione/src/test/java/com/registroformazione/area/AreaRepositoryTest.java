package com.registroformazione.area;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.github.javafaker.Faker;
import com.registroformazione.model.Area;
import com.registroformazione.repository.AreaRepository;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode;

@DataJpaTest
@AutoConfigureEmbeddedDatabase(provider = DatabaseProvider.ZONKY,refresh = RefreshMode.AFTER_EACH_TEST_METHOD)
class AreaRepositoryTest {

	@Autowired
	private AreaRepository underTest;

	@Test
	void createAndFindbyId() {
		// given
		String nome = "A80";
		Area area = new Area();
		area.setNome(nome);
		underTest.save(area);

		// when
		Optional<Area> expected = underTest.findById(area.getId());

		// then
		assertThat(expected.get().getId()).isEqualTo(1);
		assertThat(expected.get().getNome()).isEqualTo("A80");
	}

	@Test
	void Update() {
		// given
		String oldNome = "A80";
		Area oldArea = new Area();
		oldArea.setNome(oldNome);
		
		Area oldAreaSaved = underTest.save(oldArea);


		String nome = "A90";
		Area area = new Area();
		area.setId(oldAreaSaved.getId());
		area.setNome(nome);
		
		//when
		Area expected = underTest.save(area);

		//then
		assertThat(expected.getNome()).isEqualTo(nome);
		assertThat(expected.getId()).isEqualTo(area.getId());

	}
	
	@Test
	void Findall() {
		//given		
		underTest.save(new Area(null, "A90"));
		underTest.save(new Area(null, "A51"));
		underTest.save(new Area(null, "A80"));
		//when
		List<Area> expected = underTest.findAll();

		//then
        assertEquals(3, expected.size());
	}
	
	@Test
	void deleteById() {
		//given
		String nome = "A80";
		Area area = new Area();
		area.setNome(nome);
		
		underTest.save(area);
		
        assertThat(underTest.findById(1)).isPresent();
		
        //when
		underTest.deleteById(1);

		//then
        assertThat(underTest.findById(1)).isEmpty();
	}

}
