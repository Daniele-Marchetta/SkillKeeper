package com.registroformazione.persone;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
import com.registroformazione.filters.builders.PersonaPredicatesBuilder;
import com.registroformazione.model.Area;
import com.registroformazione.model.Persona;
import com.registroformazione.repository.PersonaRepository;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode;

@DataJpaTest
@AutoConfigureEmbeddedDatabase(provider = DatabaseProvider.ZONKY,refresh = RefreshMode.AFTER_EACH_TEST_METHOD)
 class PersonaRepositoryTest {
    
    @Autowired
    PersonaRepository underTest;
    
    private Persona pers = new Persona();
    
    @BeforeEach
    public void init() {
        
        pers.setNome("Daniele");
        pers.setCognome("Rossi");
        pers.setDeleted(false);
        pers.setInForza(false);
        pers.setArea(null);
        pers.setCc(null);
      
    }
    
    
    @Test
    void createAndFindbyId() {

        // when
        underTest.save(pers);
        Optional<Persona> expected = underTest.findById(pers.getId());

        // then
        assertThat(expected.get().getId()).isEqualTo(1);
        assertThat(expected.get().getNome()).isEqualTo("Daniele");
    }

    @Test
    void Update() {
        
        Persona oldPersonaSaved = underTest.save(pers);


       Persona personaUpdated = new Persona(oldPersonaSaved.getId(), "Mario", "Galli", false, false, null, null, null) ;
        //when
        Persona expected = underTest.save(personaUpdated);

        //then
        assertThat(expected.getNome()).isEqualTo("Mario");
        assertThat(expected.getId()).isEqualTo(oldPersonaSaved.getId());

    }
    
    @Test
    void Findall() {
        
        underTest.save(new Persona(null, "Mario", "Galli", false, false, null, null, null));
        underTest.save(new Persona(null, "Mario", "Verdi", false, false, null, null, null));
        underTest.save(new Persona(null, "Mario", "Rossi", false, false, null, null, null));
        underTest.save(new Persona(null, "Ilyas", "Haddad", false, false, null, null, null));

        Integer offset=1;
        Integer pageSize=10;
        
        Page<Persona> personeTest1 = underTest.findAll(predicateBuilder("nome:Mario"), PageRequest.of(offset - 1, pageSize).withSort(Sort.by("id").descending()));
        assertThat(personeTest1.get().count()).isEqualTo(3);
        
        Page<Persona> personeTest2 = underTest.findAll(predicateBuilder("cognome:Had"), PageRequest.of(offset - 1, pageSize).withSort(Sort.by("id").descending()));
        assertThat(personeTest2.get().count()).isEqualTo(1);
        
        Page<Persona> personeTest3 = underTest.findAll(predicateBuilder("nome:m,cognome:rossi"), PageRequest.of(offset - 1, pageSize).withSort(Sort.by("id").descending()));
        assertThat(personeTest3.get().count()).isEqualTo(1);
        
        Page<Persona> personeTest4 = underTest.findAll(predicateBuilder("nome:alessia"), PageRequest.of(offset - 1, pageSize).withSort(Sort.by("id").descending()));
        assertThat(personeTest4.get().count()).isZero();
    }
    
    
     BooleanExpression predicateBuilder(String searchQuery) {
         
         PersonaPredicatesBuilder builder = new PersonaPredicatesBuilder();
         Pattern pattern = Pattern.compile("(\\w+?)([:<>])(\\w+?),");
         Matcher matcher = pattern.matcher(searchQuery + ",");
         while (matcher.find()) {
             builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
         }         
         return builder.build();  
    }
    


}
