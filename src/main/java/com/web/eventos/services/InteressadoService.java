package com.web.eventos.services;

import com.web.eventos.entities.Evento;
import com.web.eventos.entities.Interessado;
import com.web.eventos.entities.Usuario;
import com.web.eventos.repositories.EventoRepository;
import com.web.eventos.repositories.InteressadoRepository;
import com.web.eventos.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class InteressadoService {

    private final InteressadoRepository interessadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EventoRepository eventoRepository;

    public InteressadoService(InteressadoRepository interessadoRepository,
            UsuarioRepository usuarioRepository,
            EventoRepository eventoRepository) {
        this.interessadoRepository = interessadoRepository;
        this.usuarioRepository = usuarioRepository;
        this.eventoRepository = eventoRepository;
    }

    public boolean isInteressado(Integer usuarioId, Integer eventoId) {
        return interessadoRepository.findByUsuarioIdAndEventoId(usuarioId, eventoId).isPresent();
    }

    @Transactional
    public void salvarInteresse(Integer usuarioId, Integer eventoId) {
        if (isInteressado(usuarioId, eventoId)) {
            throw new IllegalArgumentException("Usuário já está interessado neste evento.");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado."));

        Interessado interessado = Interessado.builder()
                .usuario(usuario)
                .evento(evento)
                .build();

        interessadoRepository.save(interessado);
    }

    @Transactional
    public void excluirInteresse(Integer usuarioId, Integer eventoId) {
        Optional<Interessado> interessadoOpt = interessadoRepository.findByUsuarioIdAndEventoId(usuarioId, eventoId);
        if (interessadoOpt.isEmpty()) {
            throw new IllegalArgumentException("Interesse não encontrado.");
        }

        interessadoRepository.delete(interessadoOpt.get());
    }

    public long countInteressadosByEventoId(Integer eventoId) {
        return interessadoRepository.countByEventoId(eventoId);
    }
}