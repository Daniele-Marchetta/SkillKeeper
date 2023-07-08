package com.registroformazione.cc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.github.javafaker.Faker;
import com.registroformazione.model.Cc;
import com.registroformazione.repository.CcRepository;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode;

@DataJpaTest
@AutoConfigureEmbeddedDatabase(provider = DatabaseProvider.ZONKY,refresh = RefreshMode.AFTER_EACH_TEST_METHOD)
class CcRepositoryTest {

    @Autowired
    private CcRepository underTest;

    @Test
    void createAndFindbyId() {
        // given
        String nome = "Aws";
        Cc cc = new Cc();
        cc.setNome(nome);
        underTest.save(cc);

        // when
        Optional<Cc> expected = underTest.findById(cc.getId());

        // then
        assertThat(expected.get().getId()).isEqualTo(1);
        assertThat(expected.get().getNome()).isEqualTo("Aws");
    }

    @Test
    void Update() {
        // given
        String oldNome = "Aws";
        Cc oldCc = new Cc();
        oldCc.setNome(oldNome);
        underTest.save(oldCc);
        
        Cc oldCcSaved = underTest.save(oldCc);


        String nome = "Scrum";
        Cc cc = new Cc();
        cc.setId(oldCcSaved.getId());
        cc.setNome(nome);
        
        //when
        Cc expected = underTest.save(cc);

        //then
        assertThat(expected.getNome()).isEqualTo(nome);
        assertThat(expected.getId()).isEqualTo(cc.getId());

    }
    
    @Test
    void Findall() {
        //given     
        underTest.save(new Cc(null, "Scrum", null));
        underTest.save(new Cc(null, "Scrum", null));
        underTest.save(new Cc(null, "Aws", null));
        //when
        List<Cc> expected = underTest.findAll();

        //then
        assertEquals(3, expected.size());
    }
    
    @Test
    void deleteById() {
        //given
        String nome = "Aws";
        Cc cc = new Cc();
        cc.setNome(nome);
        
        underTest.save(cc);
        
        assertThat(underTest.findById(1)).isPresent();
        
        //when
        underTest.deleteById(1);

        //then
        assertThat(underTest.findById(1)).isEmpty();
    }

}
