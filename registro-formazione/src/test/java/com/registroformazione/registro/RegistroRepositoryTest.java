package com.registroformazione.registro;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.github.javafaker.Faker;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.registroformazione.filters.builders.RegistroPredicatesBuilder;
import com.registroformazione.model.Attivita;
import com.registroformazione.model.Persona;
import com.registroformazione.model.Registro;
import com.registroformazione.model.Stato;
import com.registroformazione.repository.AttivitaRepository;
import com.registroformazione.repository.PersonaRepository;
import com.registroformazione.repository.RegistroRepository;
import com.registroformazione.repository.StatoRepository;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode;

//per testare la repository si occupa di gestire il contesto automaticamente
@DataJpaTest
//usa un embedded db con zonky(no docker)
@AutoConfigureEmbeddedDatabase(provider = DatabaseProvider.ZONKY ,refresh = RefreshMode.AFTER_EACH_TEST_METHOD)
 class RegistroRepositoryTest {
    
    @Autowired
    private RegistroRepository underTest;
    @Autowired
    private PersonaRepository  prepo;
    @Autowired
    private StatoRepository srepo;
    @Autowired
    private AttivitaRepository arepo;
    
    private Registro registro= new Registro();
    private Persona persona = new Persona();
    private Stato stato = new Stato();
    private Attivita attivita = new Attivita();
    
    
    @BeforeEach
    public void init() {
        Faker faker = new Faker();    
        persona.setNome(faker.name().firstName());
        prepo.save(persona);
        stato.setNome("Eseguito");
        srepo.save(stato);
        attivita.setNome("Agile e Scrum");
        arepo.save(attivita);       
        registro.setAnno(2023);
        registro.setAttivita(attivita);
        registro.setDataCompletamento(LocalDate.of(2023, 5, 17));
        registro.setDataScadenza(LocalDate.of(2024, 5, 17));
        registro.setNota(null);
        persona.setId(1);
        persona.setNome("Lerry");
        registro.setPersona(persona);
        registro.setStato(stato);
        registro.setTipo("Certificazione");
    }
   // id tipo nota anno datacompl datascad persona attivita stato deleted
    
    @Test void Findall2() {
    underTest.save(new Registro(null, "Certificazione", null, null, null, null, persona, attivita, stato, false)); 
    underTest.save(new Registro(null, "Esame", null, null, null, null, persona, attivita, stato, false));
    underTest.save(new Registro(null, "Corso", null, null, null, null, persona, attivita, stato, false));
    underTest.save(new Registro(null, "Esame", null, null, null, null, persona, attivita, stato, false));
    Integer offset=1; Integer pageSize=10; String searchQuery="tipo:Esame"; 
    RegistroPredicatesBuilder builder = new RegistroPredicatesBuilder(); 
    Pattern pattern = Pattern.compile("(\\w+?)([:<>])(\\w+?),"); 
    Matcher matcher = pattern.matcher(searchQuery + ","); 
    while (matcher.find()) { 
    builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
    } 
    BooleanExpression exp = builder.build(); 
    Page<Registro> registro = underTest.findAll(exp, PageRequest.of(offset - 1, pageSize).withSort(Sort.by("id").descending())); 
    assertThat(registro.get().count()).isEqualTo(2);
    }
    
    @Test
    void createAndFindbyId() {
        
        underTest.save(registro);

        // when
        Optional<Registro> expected = underTest.findById(registro.getId());

        // then
        assertThat(expected.get().getId()).isEqualTo(1);
        assertThat(expected.get().getAnno()).isEqualTo(2023);
    }

    @Test
    void Update() {
        // given
        
        Registro oldRegistroSaved = underTest.save(registro);

        Registro newRegistro = new Registro();
        newRegistro.setId(oldRegistroSaved.getId());
        newRegistro.setAnno(2024);
        
        //when
        Registro expected = underTest.save(newRegistro);

        //then
        assertThat(expected.getAnno()).isEqualTo(2024);
        assertThat(expected.getId()).isEqualTo(oldRegistroSaved.getId());

    }
    
    @Test
    void Findall() {
        //given     
        underTest.save(registro);
        underTest.save(new Registro(null, "Esame", null, null, null, null, persona, attivita, stato, false));
        
        //when
        List<Registro> expected = underTest.findAll();

        //then
        assertEquals(2, expected.size());
    }
    
    @Test
    void deleteById() {

        
        underTest.save(registro);
        
        assertThat(underTest.findById(1)).isPresent();
        
        //when
        underTest.deleteById(1);

        //then
        assertThat(underTest.findById(1)).isEmpty();
    }

}
