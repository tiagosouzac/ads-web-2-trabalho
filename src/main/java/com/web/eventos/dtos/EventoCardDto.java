package com.web.eventos.dtos;

import com.web.eventos.entities.Evento;

public class EventoCardDto {
    private final Evento evento;
    private final String imagemUrl;

    public EventoCardDto(Evento evento, String imagemUrl) {
        this.evento = evento;
        this.imagemUrl = imagemUrl;
    }

    public Evento getEvento() {
        return evento;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }
}