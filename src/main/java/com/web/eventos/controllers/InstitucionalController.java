package com.web.eventos.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InstitucionalController {

    @GetMapping("/sobre")
    public String sobre() {
        return "institucionais/sobre";
    }

    @GetMapping("/termos-de-uso")
    public String termosDeUso() {
        return "institucionais/termos-de-uso";
    }

    @GetMapping("/politica-de-privacidade")
    public String politicaDePrivacidade() {
        return "institucionais/politica-de-privacidade";
    }
}