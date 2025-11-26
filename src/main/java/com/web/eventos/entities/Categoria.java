package com.web.eventos.entities;

public enum Categoria {
    CULINARIA,
    MUSICA,
    ESPORTES,
    TECNOLOGIA,
    EDUCACAO,
    ARTE,
    CULTURA,
    ENTRETENIMENTO,
    NEGOCIOS,
    SAUDE,
    MODA,
    CINEMA,
    TEATRO,
    LITERATURA,
    GAMES,
    FESTAS,
    WORKSHOPS,
    CONFERENCIAS,
    NETWORKING,
    CARIDADE;

    public String getDisplayName() {
        switch (this) {
            case CULINARIA:
                return "Culinária";
            case MUSICA:
                return "Música";
            case ESPORTES:
                return "Esportes";
            case TECNOLOGIA:
                return "Tecnologia";
            case EDUCACAO:
                return "Educação";
            case ARTE:
                return "Arte";
            case CULTURA:
                return "Cultura";
            case ENTRETENIMENTO:
                return "Entretenimento";
            case NEGOCIOS:
                return "Negócios";
            case SAUDE:
                return "Saúde";
            case MODA:
                return "Moda";
            case CINEMA:
                return "Cinema";
            case TEATRO:
                return "Teatro";
            case LITERATURA:
                return "Literatura";
            case GAMES:
                return "Games";
            case FESTAS:
                return "Festas";
            case WORKSHOPS:
                return "Workshops";
            case CONFERENCIAS:
                return "Conferências";
            case NETWORKING:
                return "Networking";
            case CARIDADE:
                return "Caridade";
            default:
                return name();
        }
    }

    public String getColorClass() {
        switch (this) {
            case CULINARIA:
                return "bg-orange-500 text-white";
            case MUSICA:
                return "bg-purple-500 text-white";
            case ESPORTES:
                return "bg-green-500 text-white";
            case TECNOLOGIA:
                return "bg-blue-500 text-white";
            case EDUCACAO:
                return "bg-yellow-500 text-black";
            case ARTE:
                return "bg-pink-500 text-white";
            case CULTURA:
                return "bg-indigo-500 text-white";
            case ENTRETENIMENTO:
                return "bg-red-500 text-white";
            case NEGOCIOS:
                return "bg-gray-500 text-white";
            case SAUDE:
                return "bg-teal-500 text-white";
            case MODA:
                return "bg-rose-500 text-white";
            case CINEMA:
                return "bg-slate-500 text-white";
            case TEATRO:
                return "bg-amber-500 text-black";
            case LITERATURA:
                return "bg-lime-500 text-black";
            case GAMES:
                return "bg-cyan-500 text-white";
            case FESTAS:
                return "bg-violet-500 text-white";
            case WORKSHOPS:
                return "bg-emerald-500 text-white";
            case CONFERENCIAS:
                return "bg-sky-500 text-white";
            case NETWORKING:
                return "bg-stone-500 text-white";
            case CARIDADE:
                return "bg-fuchsia-500 text-white";
            default:
                return "bg-gray-300 text-black";
        }
    }
}
