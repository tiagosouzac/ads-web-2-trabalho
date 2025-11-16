package com.web.eventos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.web.eventos.entities.Midia;

import java.util.List;

public interface MidiaRepository extends JpaRepository<Midia, Integer> {
    List<Midia> findByEventoId(Integer eventoId);

    List<Midia> findByLocalId(Integer localId);
}
