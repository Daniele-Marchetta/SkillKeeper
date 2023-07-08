package com.registroformazione.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.registroformazione.model.Vendor;

public interface VendorRepository extends JpaRepository<Vendor, Integer> {
    public Optional<Vendor> findByNome(String vendor);

}
