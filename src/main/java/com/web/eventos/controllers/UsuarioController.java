package com.web.eventos.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.web.eventos.entities.Usuario;
import com.web.eventos.security.CustomUserDetails;
import com.web.eventos.services.AutenticacaoService;
import com.web.eventos.services.UsuarioService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final AutenticacaoService autenticacaoService;

    public UsuarioController(UsuarioService usuarioService, AutenticacaoService autenticacaoService) {
        this.usuarioService = usuarioService;
        this.autenticacaoService = autenticacaoService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("avatar");
    }

    @GetMapping("/cadastrar")
    public String cadastrar(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuarios/cadastrar";
    }

    @PostMapping("/cadastrar")
    public String salvar(@ModelAttribute @Valid Usuario usuario, BindingResult result,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar, Model model) {
        if (result.hasErrors()) {
            return "usuarios/cadastrar";
        }

        try {
            usuarioService.salvar(usuario, avatar);
            model.addAttribute("mensagem",
                    "Cadastro realizado com sucesso! Por favor, faça o login para acessar sua conta.");
            model.addAttribute("email", usuario.getEmail());
            return "entrar";
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("E-mail")) {
                result.rejectValue("email", "error.usuario", e.getMessage());
            } else if (e.getMessage().contains("CPF")) {
                result.rejectValue("cpf", "error.usuario", e.getMessage());
            } else {
                model.addAttribute("erro", e.getMessage());
            }
            return "usuarios/cadastrar";
        } catch (java.io.IOException e) {
            model.addAttribute("erro", "Erro ao fazer upload do avatar: " + e.getMessage());
            return "usuarios/cadastrar";
        } catch (Exception e) {
            model.addAttribute("erro", "Ocorreu um erro ao processar o cadastro. Tente novamente.");
            e.printStackTrace();
            return "usuarios/cadastrar";
        }
    }

    @GetMapping("/perfil/editar")
    @PreAuthorize("isAuthenticated()")
    public String editar(@AuthenticationPrincipal CustomUserDetails usuarioLogado, Model model) {
        Usuario usuario = usuarioService.findById(usuarioLogado.getId());
        model.addAttribute("usuario", usuario);
        return "usuarios/editar";
    }

    @PostMapping("/perfil/editar")
    @PreAuthorize("isAuthenticated()")
    public String atualizar(@AuthenticationPrincipal CustomUserDetails usuarioLogado,
            @ModelAttribute @Valid Usuario usuario, BindingResult result,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar, Model model) {
        if (result.hasErrors()) {
            return "usuarios/editar";
        }

        try {
            usuario.setId(usuarioLogado.getId());
            Usuario usuarioAtualizado = usuarioService.atualizar(usuario, avatar);
            autenticacaoService.atualizarContextoAutenticacao(usuarioAtualizado.getEmail());
            model.addAttribute("mensagem", "Perfil atualizado com sucesso!");
            model.addAttribute("usuario", usuarioAtualizado);
            return "usuarios/editar";
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("E-mail")) {
                result.rejectValue("email", "error.usuario", e.getMessage());
            } else if (e.getMessage().contains("CPF")) {
                result.rejectValue("cpf", "error.usuario", e.getMessage());
            } else {
                model.addAttribute("erro", e.getMessage());
            }
            return "usuarios/editar";
        } catch (java.io.IOException e) {
            model.addAttribute("erro", "Erro ao fazer upload do avatar: " + e.getMessage());
            return "usuarios/editar";
        } catch (Exception e) {
            model.addAttribute("erro", "Ocorreu um erro ao atualizar o perfil. Tente novamente.");
            e.printStackTrace();
            return "usuarios/editar";
        }
    }

    @GetMapping("/excluir")
    @PreAuthorize("isAuthenticated()")
    public String excluir(@AuthenticationPrincipal CustomUserDetails usuarioLogado) {
        usuarioService.excluir(usuarioLogado.getId());
        return "redirect:/logout";
    }
}
