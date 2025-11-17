package com.web.eventos.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AutenticacaoController {
    @GetMapping("/entrar")
    public String entrar() {
        return "entrar";
    }
}
