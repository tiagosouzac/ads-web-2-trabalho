package com.web.eventos.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.web.eventos.services.EventoService;

@Controller
public class HomeController {
    private final EventoService eventoService;

    public HomeController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @GetMapping
    public String home(Model model) {
        model.addAttribute("eventosPorCategoria", eventoService.getEventosPorCategoria());
        return "index";
    }
}
