package com.registroformazione.competenza;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.github.javafaker.Faker;
import com.registroformazione.model.Competenza;
import com.registroformazione.repository.CompetenzaRepository;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode;

@DataJpaTest
@AutoConfigureEmbeddedDatabase(provider = DatabaseProvider.ZONKY,refresh = RefreshMode.AFTER_EACH_TEST_METHOD)
class CompetenzaRepositoryTest {

    @Autowired
    private CompetenzaRepository underTest;

    @Test
    void createAndFindbyId() {
        // given
        String nome = "Servizi Aws";
        Competenza competenza = new Competenza();
        competenza.setNome(nome);
        underTest.save(competenza);

        // when
        Optional<Competenza> expected = underTest.findById(competenza.getId());

        // then
        assertThat(expected.get().getId()).isEqualTo(1);
        assertThat(expected.get().getNome()).isEqualTo("Servizi Aws");
    }

    @Test
    void Update() {
        // given
        String oldNome = "Servizi Aws";
        Competenza oldCompetenza = new Competenza();
        oldCompetenza.setNome(oldNome);
        underTest.save(oldCompetenza);
        
        Competenza oldCompetenzaSaved = underTest.save(oldCompetenza);


        String nome = "Cloud Security";
        Competenza competenza = new Competenza();
        competenza.setId(oldCompetenzaSaved.getId());
        competenza.setNome(nome);
        
        //when
        Competenza expected = underTest.save(competenza);

        //then
        assertThat(expected.getNome()).isEqualTo(nome);
        assertThat(expected.getId()).isEqualTo(competenza.getId());

    }
    
    @Test
    void Findall() {
        //given     
        underTest.save(new Competenza(null, "Cloud Security", null));
        underTest.save(new Competenza(null, "Agile and Scrum", null));
        underTest.save(new Competenza(null, "Servizi Aws", null));
        //when
        List<Competenza> expected = underTest.findAll();

        //then
        assertEquals(3, expected.size());
    }
    
    @Test
    void deleteById() {
        //given
        String nome = "Servizi Aws";
        Competenza competenza = new Competenza();
        competenza.setNome(nome);
        
        underTest.save(competenza);
        
        assertThat(underTest.findById(1)).isPresent();
        
        //when
        underTest.deleteById(1);

        //then
        assertThat(underTest.findById(1)).isEmpty();
    }

}
