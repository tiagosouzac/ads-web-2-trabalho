package com.web.eventos.services;

import org.springframework.stereotype.Service;

import com.web.eventos.dtos.EventoCardDto;
import com.web.eventos.entities.Categoria;
import com.web.eventos.entities.Evento;
import com.web.eventos.entities.EventoStatus;
import com.web.eventos.entities.Midia;
import com.web.eventos.entities.MidiaTipo;
import com.web.eventos.entities.Organizacao;
import com.web.eventos.repositories.EventoRepository;
import com.web.eventos.repositories.MidiaRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        existing.setPreco(evento.getPreco());
        existing.setIdadeMinima(evento.getIdadeMinima());
        existing.setDataInicio(evento.getDataInicio());
        existing.setDataFim(evento.getDataFim());
        existing.setStatus(evento.getStatus());

        return eventoRepository.save(existing);
    }

    public void excluir(Integer id) {
        List<Midia> midias = midiaRepository.findByEventoId(id);
        midiaRepository.deleteAll(midias);

        eventoRepository.deleteById(id);
    }

    public Map<Categoria, List<EventoCardDto>> getEventosPorCategoria() {
        Map<Categoria, List<EventoCardDto>> map = new HashMap<>();

        for (Categoria cat : Categoria.values()) {
            List<Evento> eventos = eventoRepository.findFirst4ByCategoriaAndStatus(cat, EventoStatus.PUBLICADO);

            if (!eventos.isEmpty()) {
                List<EventoCardDto> eventoCards = eventos.stream().map(evento -> {
                    String imagemUrl = midiaRepository.findFirstByEventoIdAndTipo(evento.getId(), MidiaTipo.IMAGEM)
                            .map(Midia::getUrl)
                            .orElse(null);

                    return new EventoCardDto(evento, imagemUrl);
                }).collect(Collectors.toList());

                map.put(cat, eventoCards);
            }
        }

        return map;
    }
}
