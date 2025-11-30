package com.web.eventos.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.web.eventos.entities.Comentario;
import com.web.eventos.entities.Evento;
import com.web.eventos.entities.Usuario;
import com.web.eventos.security.CustomUserDetails;
import com.web.eventos.services.ComentarioService;
import com.web.eventos.services.EventoService;

@Controller
@RequestMapping("/comentarios")
public class ComentarioController {

    private final ComentarioService comentarioService;
    private final EventoService eventoService;

    public ComentarioController(ComentarioService comentarioService, EventoService eventoService) {
        this.comentarioService = comentarioService;
        this.eventoService = eventoService;
    }

    @PostMapping("/salvar")
    public String comentar(@RequestParam Integer eventoId, @RequestParam("comentario") String conteudo,
            @RequestParam(value = "nota", required = false) Integer avaliacao,
            @AuthenticationPrincipal CustomUserDetails user, RedirectAttributes redirectAttributes) {
        if (user == null || !"USUARIO".equals(user.getTipo())) {
            redirectAttributes.addFlashAttribute("error", "Apenas usuários podem comentar.");
            return "redirect:/eventos/" + eventoId;
        }

        Evento evento = eventoService.findById(eventoId);
        if (evento == null) {
            return "redirect:/erros/404";
        }

        Usuario usuario = new Usuario();
        usuario.setId(user.getId());

        Comentario comentario = Comentario.builder()
                .evento(evento)
                .usuario(usuario)
                .conteudo(conteudo)
                .avaliacao(avaliacao)
                .build();

        comentarioService.salvar(comentario);

        return "redirect:/eventos/" + eventoId;
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Integer id, @AuthenticationPrincipal CustomUserDetails user,
            RedirectAttributes redirectAttributes) {
        if (user == null || !"USUARIO".equals(user.getTipo())) {
            redirectAttributes.addFlashAttribute("error", "Apenas usuários podem excluir comentários.");
            return "redirect:/eventos";
        }

        Comentario comentario = comentarioService.findById(id);
        if (comentario == null) {
            redirectAttributes.addFlashAttribute("error", "Comentário não encontrado.");
            return "redirect:/eventos";
        }

        if (!comentario.getUsuario().getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("error", "Você não pode excluir este comentário.");
            return "redirect:/eventos/" + comentario.getEvento().getId();
        }

        Integer eventoId = comentario.getEvento().getId();
        comentarioService.excluir(id);
        redirectAttributes.addFlashAttribute("success", "Comentário excluído com sucesso.");
        return "redirect:/eventos/" + eventoId;
    }

}