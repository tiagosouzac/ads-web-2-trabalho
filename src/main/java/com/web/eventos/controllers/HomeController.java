package com.web.eventos.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.web.eventos.services.BannerService;
import com.web.eventos.services.EventoService;

@Controller
public class HomeController {
    private final EventoService eventoService;
    private final BannerService bannerService;

    public HomeController(EventoService eventoService, BannerService bannerService) {
        this.eventoService = eventoService;
        this.bannerService = bannerService;
    }

    @GetMapping
    public String home(Model model) {
        model.addAttribute("eventosPorCategoria", eventoService.getEventosPorCategoria());
        model.addAttribute("banners", bannerService.getBanners());
        return "index";
    }
}
