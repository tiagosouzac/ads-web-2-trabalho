package com.web.eventos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.web.eventos.entities.Midia;
import com.web.eventos.entities.MidiaTipo;

import java.util.List;
import java.util.Optional;

public interface MidiaRepository extends JpaRepository<Midia, Integer> {
    List<Midia> findByEventoId(Integer eventoId);

    List<Midia> findByLocalId(Integer localId);

    Optional<Midia> findFirstByEventoIdAndTipo(Integer eventoId, MidiaTipo tipo);
}
