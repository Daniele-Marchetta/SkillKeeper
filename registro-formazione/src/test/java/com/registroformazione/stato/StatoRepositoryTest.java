package com.registroformazione.stato;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.github.javafaker.Faker;
import com.registroformazione.model.Stato;
import com.registroformazione.repository.StatoRepository;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode;

@DataJpaTest
@AutoConfigureEmbeddedDatabase(provider = DatabaseProvider.ZONKY,refresh = RefreshMode.AFTER_EACH_TEST_METHOD)
class StatoRepositoryTest {

    @Autowired
    private StatoRepository underTest;

    @Test
    void createAndFindbyId() {
        // given
        String nome = "Eseguito";
        Stato stato = new Stato();
        stato.setNome(nome);
        underTest.save(stato);

        // when
        Optional<Stato> expected = underTest.findById(stato.getId());

        // then
        assertThat(expected.get().getId()).isEqualTo(1);
        assertThat(expected.get().getNome()).isEqualTo("Eseguito");
    }

    @Test
    void Update() {
        // given
        String oldNome = "Eseguito";
        Stato oldStato = new Stato();
        oldStato.setNome(oldNome);
        underTest.save(oldStato);
        
        Stato oldStatoSaved = underTest.save(oldStato);


        String nome = "Pianificato";
        Stato stato = new Stato();
        stato.setId(oldStatoSaved.getId());
        stato.setNome(nome);
        
        //when
        Stato expected = underTest.save(stato);

        //then
        assertThat(expected.getNome()).isEqualTo(nome);
        assertThat(expected.getId()).isEqualTo(stato.getId());

    }
    
    @Test
    void Findall() {
        //given     
        underTest.save(new Stato(null, "Pianificato", null));
        underTest.save(new Stato(null, "Ipotesi", null));
        underTest.save(new Stato(null, "Eseguito", null));
        //when
        List<Stato> expected = underTest.findAll();

        //then
        assertEquals(3, expected.size());
    }
    
    @Test
    void deleteById() {
        //given
        String nome = "Eseguito";
        Stato stato = new Stato();
        stato.setNome(nome);
        
        underTest.save(stato);
        
        assertThat(underTest.findById(1)).isPresent();
        
        //when
        underTest.deleteById(1);

        //then
        assertThat(underTest.findById(1)).isEmpty();
    }

}
