package com.web.eventos.controllers;

import com.web.eventos.security.CustomUserDetails;
import com.web.eventos.services.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/banners")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {
    private final BannerService bannerService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("banners", bannerService.getBanners());
        return "admin/banners";
    }

    @GetMapping("/cadastrar")
    public String cadastrar() {
        return "admin/banners/cadastrar";
    }

    @PostMapping("/cadastrar")
    public String salvar(@AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String titulo,
            @RequestParam MultipartFile imagem,
            RedirectAttributes redirectAttributes) {
        try {
            bannerService.salvarComUpload(imagem, titulo);
            redirectAttributes.addFlashAttribute("mensagem", "Banner adicionado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao adicionar banner: " + e.getMessage());
        }
        return "redirect:/admin/banners";
    }

    @PostMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            bannerService.excluir(id);
            redirectAttributes.addFlashAttribute("mensagem", "Banner removido com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao remover banner: " + e.getMessage());
        }
        return "redirect:/admin/banners";
    }
}