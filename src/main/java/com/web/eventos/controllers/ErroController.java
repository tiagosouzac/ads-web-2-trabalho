package com.web.eventos.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/erros")
public class ErroController {

    @GetMapping("/acesso-negado")
    public String acessoNegado() {
        return "erros/acesso-negado";
    }
}