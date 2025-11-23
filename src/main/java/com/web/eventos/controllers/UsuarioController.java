package com.web.eventos.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.web.eventos.entities.Usuario;
import com.web.eventos.services.UsuarioService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
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

    @PostMapping
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
}
