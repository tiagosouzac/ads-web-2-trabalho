package com.web.eventos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.web.eventos.entities.Interessado;

import java.util.List;

public interface InteressadoRepository extends JpaRepository<Interessado, Integer> {
    Long countByEventoId(Integer eventoId);

    List<Interessado> findByUsuarioId(Integer usuarioId);
}
