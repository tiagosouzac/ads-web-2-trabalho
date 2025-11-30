package com.web.eventos.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.web.eventos.entities.Comentario;

import java.util.List;

public interface ComentarioRepository extends JpaRepository<Comentario, Integer> {
    List<Comentario> findByEventoIdOrderByCriadoEmDesc(Integer eventoId);

    List<Comentario> findByEventoIdAndUsuarioIdOrderByCriadoEmDesc(Integer eventoId, Integer usuarioId);

    Page<Comentario> findByEventoIdAndUsuarioIdNotOrderByCriadoEmDesc(Integer eventoId, Integer usuarioId,
            Pageable pageable);

    Page<Comentario> findByEventoIdOrderByCriadoEmDesc(Integer eventoId, Pageable pageable);

    long countByEventoId(Integer eventoId);
}
