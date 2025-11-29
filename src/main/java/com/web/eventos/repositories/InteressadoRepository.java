package com.web.eventos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.web.eventos.entities.Interessado;

import java.util.Optional;

public interface InteressadoRepository extends JpaRepository<Interessado, Integer> {
    Long countByEventoId(Integer eventoId);

    Optional<Interessado> findByUsuarioIdAndEventoId(Integer usuarioId, Integer eventoId);
}
