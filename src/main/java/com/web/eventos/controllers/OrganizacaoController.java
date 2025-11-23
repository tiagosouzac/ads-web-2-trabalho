package com.web.eventos.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.web.eventos.entities.Organizacao;
import com.web.eventos.services.OrganizacaoService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/organizacoes")
public class OrganizacaoController {
    private final OrganizacaoService organizacaoService;

    public OrganizacaoController(OrganizacaoService organizacaoService) {
        this.organizacaoService = organizacaoService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("logo");
    }

    @GetMapping("/cadastrar")
    public String cadastrar(Model model) {
        model.addAttribute("organizacao", new Organizacao());
        return "organizacoes/cadastrar";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute @Valid Organizacao organizacao, BindingResult result,
            @RequestParam(value = "logo", required = false) MultipartFile logo, Model model) {
        if (result.hasErrors()) {
            return "organizacoes/cadastrar";
        }

        try {
            organizacaoService.salvar(organizacao, logo);
            model.addAttribute("mensagem",
                    "Cadastro realizado com sucesso! Por favor, faça o login para acessar sua conta.");
            model.addAttribute("email", organizacao.getEmail());
            return "entrar";
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("E-mail")) {
                result.rejectValue("email", "error.organizacao", e.getMessage());
            } else if (e.getMessage().contains("CNPJ")) {
                result.rejectValue("cnpj", "error.organizacao", e.getMessage());
            } else {
                model.addAttribute("erro", e.getMessage());
            }
            return "organizacoes/cadastrar";
        } catch (java.io.IOException e) {
            model.addAttribute("erro", "Erro ao fazer upload do logo: " + e.getMessage());
            return "organizacoes/cadastrar";
        } catch (Exception e) {
            model.addAttribute("erro", "Ocorreu um erro ao processar o cadastro. Tente novamente.");
            e.printStackTrace();
            return "organizacoes/cadastrar";
        }
    }
}
