package com.registroformazione.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.registroformazione.model.Area;
@Repository
public interface AreaRepository extends JpaRepository<Area, Integer> {

   public Optional<Area> findByNome(String area);

}
