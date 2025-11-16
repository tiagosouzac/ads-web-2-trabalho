package com.web.eventos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.web.eventos.entities.Categoria;
import com.web.eventos.entities.Evento;
import com.web.eventos.entities.EventoStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Integer> {
    List<Evento> findByStatus(EventoStatus status);

    List<Evento> findByCategoria(Categoria categoria);

    List<Evento> findByDataInicioBetween(LocalDateTime inicio, LocalDateTime fim);
}
