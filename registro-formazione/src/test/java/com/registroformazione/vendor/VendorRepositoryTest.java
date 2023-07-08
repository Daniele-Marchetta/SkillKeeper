package com.registroformazione.vendor;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.github.javafaker.Faker;
import com.registroformazione.model.Vendor;
import com.registroformazione.repository.VendorRepository;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode;

@DataJpaTest
@AutoConfigureEmbeddedDatabase(provider = DatabaseProvider.ZONKY,refresh = RefreshMode.AFTER_EACH_TEST_METHOD)
class VendorRepositoryTest {

    @Autowired
    private VendorRepository underTest;

    @Test
    void createAndFindbyId() {
        // given
        String nome = "Amazon";
        Vendor vendor = new Vendor();
        vendor.setNome(nome);
        underTest.save(vendor);

        // when
        Optional<Vendor> expected = underTest.findById(vendor.getId());

        // then
        assertThat(expected.get().getId()).isEqualTo(1);
        assertThat(expected.get().getNome()).isEqualTo("Amazon");
    }

    @Test
    void Update() {
        // given
        String oldNome = "Amazon";
        Vendor oldVendor = new Vendor();
        oldVendor.setNome(oldNome);
        underTest.save(oldVendor);
        
        Vendor oldVendorSaved = underTest.save(oldVendor);


        String nome = "Microsoft";
        Vendor vendor = new Vendor();
        vendor.setId(oldVendorSaved.getId());
        vendor.setNome(nome);
        
        //when
        Vendor expected = underTest.save(vendor);

        //then
        assertThat(expected.getNome()).isEqualTo(nome);
        assertThat(expected.getId()).isEqualTo(vendor.getId());

    }
    
    @Test
    void Findall() {
        //given     
        underTest.save(new Vendor(null, "Microsoft", null));
        underTest.save(new Vendor(null, "Informatica", null));
        underTest.save(new Vendor(null, "Amazon", null));
        //when
        List<Vendor> expected = underTest.findAll();

        //then
        assertEquals(3, expected.size());
    }
    
    @Test
    void deleteById() {
        //given
        String nome = "Amazon";
        Vendor vendor = new Vendor();
        vendor.setNome(nome);
        
        underTest.save(vendor);
        
        assertThat(underTest.findById(1)).isPresent();
        
        //when
        underTest.deleteById(1);

        //then
        assertThat(underTest.findById(1)).isEmpty();
    }

}
