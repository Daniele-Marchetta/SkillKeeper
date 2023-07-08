package com.registroformazione.attivita;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.github.javafaker.Faker;
import com.registroformazione.model.Attivita;
import com.registroformazione.model.Competenza;
import com.registroformazione.model.Vendor;
import com.registroformazione.repository.AttivitaRepository;
import com.registroformazione.repository.CompetenzaRepository;
import com.registroformazione.repository.VendorRepository;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode;

//per testare la repository si occupa di gestire il contesto automaticamente
@DataJpaTest
//usa un embedded db con zonky(no docker)
@AutoConfigureEmbeddedDatabase(provider = DatabaseProvider.ZONKY ,refresh = RefreshMode.AFTER_EACH_TEST_METHOD)
 class AttivitaRepositoryTest {
    
    @Autowired
    private AttivitaRepository underTest;
    @Autowired
    private CompetenzaRepository crepo;
    @Autowired
    private VendorRepository vrepo;
    
    private Attivita attivita= new Attivita();
    private Vendor vendor = new Vendor();
    private Competenza competenza = new Competenza();
    
    
    @BeforeEach
    public void init() {
        Faker faker = new Faker();
        
        vendor.setNome(faker.name().lastName());
        vrepo.save(vendor);
        
        competenza.setNome(faker.name().lastName());
        crepo.save(competenza);
        
        attivita.setNome("azure cloud function 1");
        attivita.setCodice("z32");
        attivita.setVendor(vendor);
        attivita.setCompetenza(competenza);
    }

    @Test
    void createAndFindbyId() {
        
        underTest.save(attivita);

        // when
        Optional<Attivita> expected = underTest.findById(attivita.getId());

        // then
        assertThat(expected.get().getId()).isEqualTo(1);
        assertThat(expected.get().getNome()).isEqualTo("azure cloud function 1");
    }

    @Test
    void Update() {
        // given
        
        Attivita oldAttivitaSaved = underTest.save(attivita);


        String nome = "Microsoft cloud";
        Attivita newAttivita = new Attivita();
        newAttivita.setId(oldAttivitaSaved.getId());
        newAttivita.setNome(nome);
        
        //when
        Attivita expected = underTest.save(newAttivita);

        //then
        assertThat(expected.getNome()).isEqualTo(nome);
        assertThat(expected.getId()).isEqualTo(oldAttivitaSaved.getId());

    }
    
    @Test
    void Findall() {
        //given     
        underTest.save(attivita);
        underTest.save(new Attivita(null, "cloud 2", "z789", competenza, vendor,null));
        underTest.save(new Attivita(null, "cloud 3", "z456", competenza, vendor, null));
        //when
        List<Attivita> expected = underTest.findAll();

        //then
        assertEquals(3, expected.size());
    }
    
    @Test
    void deleteById() {

        
        underTest.save(attivita);
        
        assertThat(underTest.findById(1)).isPresent();
        
        //when
        underTest.deleteById(1);

        //then
        assertThat(underTest.findById(1)).isEmpty();
    }

}
