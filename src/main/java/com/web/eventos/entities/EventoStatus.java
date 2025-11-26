package com.web.eventos.entities;

public enum EventoStatus {
    RASCUNHO,
    PUBLICADO,
    CANCELADO,
    FINALIZADO;

    public String getDisplayName() {
        switch (this) {
            case RASCUNHO:
                return "Rascunho";
            case PUBLICADO:
                return "Publicado";
            case CANCELADO:
                return "Cancelado";
            case FINALIZADO:
                return "Finalizado";
            default:
                return name();
        }
    }
}
