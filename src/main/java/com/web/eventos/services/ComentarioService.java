package com.web.eventos.services;

import com.web.eventos.entities.Comentario;
import com.web.eventos.repositories.ComentarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ComentarioService {
    private final ComentarioRepository comentarioRepository;

    public ComentarioService(ComentarioRepository comentarioRepository) {
        this.comentarioRepository = comentarioRepository;
    }

    public List<Comentario> getComentariosPriorizados(Integer eventoId, Integer usuarioId, int limit) {
        List<Comentario> result = new ArrayList<>();
        if (usuarioId != null) {
            List<Comentario> userComments = comentarioRepository.findByEventoIdAndUsuarioIdOrderByCriadoEmDesc(eventoId,
                    usuarioId);
            if (userComments.size() > limit) {
                result.addAll(userComments.subList(0, limit));
            } else {
                result.addAll(userComments);
                int remaining = limit - userComments.size();
                if (remaining > 0) {
                    Page<Comentario> others = comentarioRepository.findByEventoIdAndUsuarioIdNotOrderByCriadoEmDesc(
                            eventoId, usuarioId, PageRequest.of(0, remaining));
                    result.addAll(others.getContent());
                }
            }
        } else {
            List<Comentario> all = comentarioRepository.findByEventoIdOrderByCriadoEmDesc(eventoId);
            result.addAll(all.subList(0, Math.min(limit, all.size())));
        }
        return result;
    }

    public Page<Comentario> getComentariosPaginados(Integer eventoId, Integer usuarioId, Pageable pageable) {
        if (usuarioId != null) {
            return comentarioRepository.findByEventoIdAndUsuarioIdNotOrderByCriadoEmDesc(eventoId, usuarioId, pageable);
        } else {
            return comentarioRepository.findByEventoIdOrderByCriadoEmDesc(eventoId, pageable);
        }
    }

    public Comentario findById(Integer id) {
        return comentarioRepository.findById(id).orElse(null);
    }

    public void excluir(Integer comentarioId) {
        comentarioRepository.deleteById(comentarioId);
    }

    public Comentario salvar(Comentario comentario) {
        return comentarioRepository.save(comentario);
    }

    public long getTotalComentarios(Integer eventoId) {
        return comentarioRepository.countByEventoId(eventoId);
    }
}