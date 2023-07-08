package com.registroformazione.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.registroformazione.model.Stato;

public interface StatoRepository extends JpaRepository<Stato, Integer> {
    public Optional<Stato> findByNome(String stato);
}
