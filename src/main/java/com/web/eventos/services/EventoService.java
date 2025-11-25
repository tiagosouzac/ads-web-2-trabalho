package com.web.eventos.services;

import org.springframework.stereotype.Service;

import com.web.eventos.entities.Evento;
import com.web.eventos.entities.Midia;
import com.web.eventos.entities.Organizacao;
import com.web.eventos.repositories.EventoRepository;
import com.web.eventos.repositories.MidiaRepository;

import java.util.List;

@Service
public class EventoService {
    private final EventoRepository eventoRepository;
    private final MidiaRepository midiaRepository;

    public EventoService(EventoRepository eventoRepository, MidiaRepository midiaRepository) {
        this.eventoRepository = eventoRepository;
        this.midiaRepository = midiaRepository;
    }

    public Evento findById(Integer id) {
        return eventoRepository.findById(id).orElse(null);
    }

    public List<Evento> findByOrganizacao(Organizacao organizacao) {
        return eventoRepository.findByOrganizacao(organizacao);
    }

    public Evento salvar(Evento evento) {
        return eventoRepository.save(evento);
    }

    public Evento atualizar(Evento evento) {
        Evento existing = eventoRepository.findById(evento.getId())
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));

        existing.setCategoria(evento.getCategoria());
        existing.setLocal(evento.getLocal());
        existing.setNome(evento.getNome());
        existing.setDescricao(evento.getDescricao());
        existing.setDataInicio(evento.getDataInicio());
        existing.setDataFim(evento.getDataFim());
        existing.setStatus(evento.getStatus());

        return eventoRepository.save(existing);
    }

    public void excluir(Integer id) {
        // Delete associated midias first
        List<Midia> midias = midiaRepository.findByEventoId(id);
        midiaRepository.deleteAll(midias);

        // Then delete the event
        eventoRepository.deleteById(id);
    }
}
