package com.registroformazione.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.registroformazione.model.Competenza;

public interface CompetenzaRepository extends JpaRepository<Competenza, Integer> {
public Optional<Competenza> findByNome(String competenza);
}
