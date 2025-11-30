package com.web.eventos.controllers;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.web.eventos.entities.Categoria;
import com.web.eventos.services.LocalService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final LocalService localService;

    public GlobalControllerAdvice(LocalService localService) {
        this.localService = localService;
    }

    @ModelAttribute("categorias")
    @Cacheable("categorias")
    public List<Categoria> obterCategorias(HttpServletRequest request) {
        request.getSession(true);
        return Arrays.asList(Categoria.values());
    }

    @ModelAttribute("cidades")
    @Cacheable("cidades")
    public List<String> obterCidades(HttpServletRequest request) {
        request.getSession(true);
        return localService.findDistinctCidade();
    }
}
