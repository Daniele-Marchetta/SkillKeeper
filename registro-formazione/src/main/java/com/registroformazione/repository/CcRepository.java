package com.registroformazione.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.registroformazione.model.Cc;

public interface CcRepository extends JpaRepository<Cc, Integer> {
    public Optional<Cc> findByNome(String cc);

}
