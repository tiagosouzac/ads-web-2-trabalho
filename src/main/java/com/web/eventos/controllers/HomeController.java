package com.web.eventos.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.web.eventos.services.BannerService;
import com.web.eventos.services.EventoService;
import com.web.eventos.services.InteressadoService;
import java.util.Map;
import java.util.HashMap;

@Controller
public class HomeController {
    private final EventoService eventoService;
    private final BannerService bannerService;
    private final InteressadoService interessadoService;

    public HomeController(EventoService eventoService, BannerService bannerService,
            InteressadoService interessadoService) {
        this.eventoService = eventoService;
        this.bannerService = bannerService;
        this.interessadoService = interessadoService;
    }

    @GetMapping
    public String home(Model model) {
        var eventosPorCategoria = eventoService.getEventosPorCategoria();

        // Calcular interessadosCount para cada evento
        Map<Integer, Long> interessadosCountMap = new HashMap<>();
        for (var categoria : eventosPorCategoria.values()) {
            for (var evento : categoria) {
                Long count = interessadoService.countInteressadosByEventoId(evento.getId());
                interessadosCountMap.put(evento.getId(), count);
            }
        }

        model.addAttribute("eventosPorCategoria", eventosPorCategoria);
        model.addAttribute("interessadosCountMap", interessadosCountMap);
        model.addAttribute("banners", bannerService.getBanners());
        return "index";
    }
}
