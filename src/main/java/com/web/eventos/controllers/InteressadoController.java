package com.web.eventos.controllers;

import com.web.eventos.security.CustomUserDetails;
import com.web.eventos.services.InteressadoService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/interessados")
public class InteressadoController {

    private final InteressadoService interessadoService;

    public InteressadoController(InteressadoService interessadoService) {
        this.interessadoService = interessadoService;
    }

    @PostMapping("/salvar")
    public String salvar(@RequestParam Integer eventoId,
            @AuthenticationPrincipal CustomUserDetails user,
            RedirectAttributes redirectAttributes) {
        if (user == null || !"USUARIO".equals(user.getTipo())) {
            return "redirect:/entrar";
        }

        try {
            interessadoService.salvarInteresse(user.getId(), eventoId);
            redirectAttributes.addFlashAttribute("success", "Interesse registrado com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao registrar interesse.");
        }

        return "redirect:/eventos/" + eventoId;
    }

    @PostMapping("/excluir")
    public String excluir(@RequestParam Integer eventoId,
            @RequestParam("_method") String method,
            @AuthenticationPrincipal CustomUserDetails user,
            RedirectAttributes redirectAttributes) {
        if (!"DELETE".equals(method)) {
            return "redirect:/eventos/" + eventoId;
        }

        if (user == null || !"USUARIO".equals(user.getTipo())) {
            return "redirect:/entrar";
        }

        try {
            interessadoService.excluirInteresse(user.getId(), eventoId);
            redirectAttributes.addFlashAttribute("success", "Interesse removido com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao remover interesse.");
        }

        return "redirect:/eventos/" + eventoId;
    }
}