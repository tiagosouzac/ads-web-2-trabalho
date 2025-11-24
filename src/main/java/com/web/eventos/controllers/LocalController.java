package com.web.eventos.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import com.web.eventos.entities.Local;
import com.web.eventos.entities.Organizacao;
import com.web.eventos.security.CustomUserDetails;
import com.web.eventos.services.LocalService;
import com.web.eventos.services.OrganizacaoService;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/locais")
public class LocalController {
    private final LocalService localService;
    private final OrganizacaoService organizacaoService;

    public LocalController(LocalService localService, OrganizacaoService organizacaoService) {
        this.localService = localService;
        this.organizacaoService = organizacaoService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated() and principal.tipo == 'ORGANIZACAO'")
    public String listar(@AuthenticationPrincipal CustomUserDetails organizacaoLogada, Model model) {
        Organizacao organizacao = organizacaoService.findById(organizacaoLogada.getId());
        model.addAttribute("locais", localService.findByOrganizacao(organizacao));
        return "locais/listar";
    }

    @GetMapping("/cadastrar")
    @PreAuthorize("isAuthenticated() and principal.tipo == 'ORGANIZACAO'")
    public String cadastrar(@AuthenticationPrincipal CustomUserDetails organizacaoLogada, Model model) {
        model.addAttribute("local", new Local());
        return "locais/cadastrar";
    }

    @PostMapping("/cadastrar")
    @PreAuthorize("isAuthenticated() and principal.tipo == 'ORGANIZACAO'")
    public String salvar(@AuthenticationPrincipal CustomUserDetails organizacaoLogada,
            @ModelAttribute @Valid Local local, BindingResult result, RedirectAttributes redirectAttributes,
            Model model) {
        if (result.hasErrors()) {
            return "locais/cadastrar";
        }

        try {
            Organizacao organizacao = organizacaoService.findById(organizacaoLogada.getId());
            local.setOrganizacao(organizacao);
            localService.salvar(local);
            redirectAttributes.addFlashAttribute("mensagem", "Local cadastrado com sucesso!");
            return "redirect:/locais";
        } catch (Exception e) {
            model.addAttribute("erro", "Ocorreu um erro ao cadastrar o local. Tente novamente.");
            e.printStackTrace();
            return "locais/cadastrar";
        }
    }

    @GetMapping("/editar/{id}")
    @PreAuthorize("isAuthenticated() and principal.tipo == 'ORGANIZACAO'")
    public String editar(@AuthenticationPrincipal CustomUserDetails organizacaoLogada, @PathVariable Integer id,
            Model model) {
        Local local = localService.findById(id);
        if (local == null) {
            model.addAttribute("erro", "Local não encontrado.");
            return "locais/listar";
        }
        if (!local.getOrganizacao().getId().equals(organizacaoLogada.getId())) {
            model.addAttribute("erro", "Você não tem permissão para editar este local.");
            return "locais/listar";
        }
        model.addAttribute("local", local);
        return "locais/editar";
    }

    @PostMapping("/editar/{id}")
    @PreAuthorize("isAuthenticated() and principal.tipo == 'ORGANIZACAO'")
    public String atualizar(@AuthenticationPrincipal CustomUserDetails organizacaoLogada, @PathVariable Integer id,
            @ModelAttribute @Valid Local local, BindingResult result, RedirectAttributes redirectAttributes,
            Model model) {
        if (result.hasErrors()) {
            return "locais/editar";
        }

        Local existing = localService.findById(id);
        if (existing == null || !existing.getOrganizacao().getId().equals(organizacaoLogada.getId())) {
            model.addAttribute("erro", "Local não encontrado ou você não tem permissão.");
            return "locais/editar";
        }

        try {
            local.setId(id);
            localService.atualizar(local);
            redirectAttributes.addFlashAttribute("mensagem", "Local atualizado com sucesso!");
            return "redirect:/locais";
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            return "locais/editar";
        } catch (Exception e) {
            model.addAttribute("erro", "Ocorreu um erro ao atualizar o local. Tente novamente.");
            e.printStackTrace();
            return "locais/editar";
        }
    }

    @GetMapping("/excluir/{id}")
    @PreAuthorize("isAuthenticated() and principal.tipo == 'ORGANIZACAO'")
    public String excluir(@AuthenticationPrincipal CustomUserDetails organizacaoLogada, @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {
        Local local = localService.findById(id);

        if (local != null && local.getOrganizacao().getId().equals(organizacaoLogada.getId())) {
            localService.excluir(id);
        }

        redirectAttributes.addFlashAttribute("mensagem", "Local excluído com sucesso!");
        return "redirect:/locais";
    }
}
