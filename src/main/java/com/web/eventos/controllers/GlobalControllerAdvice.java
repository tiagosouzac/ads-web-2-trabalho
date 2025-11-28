package com.web.eventos.controllers;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.web.eventos.entities.Categoria;
import com.web.eventos.entities.Local;
import com.web.eventos.services.LocalService;

import java.util.Arrays;
import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final LocalService localService;

    public GlobalControllerAdvice(LocalService localService) {
        this.localService = localService;
    }

    @ModelAttribute("categorias")
    public List<Categoria> obterCategorias() {
        return Arrays.asList(Categoria.values());
    }

    @ModelAttribute("locais")
    public List<Local> obterLocais() {
        return localService.findAll();
    }
}
