package com.web.eventos.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import com.web.eventos.entities.Categoria;
import com.web.eventos.entities.Evento;
import com.web.eventos.entities.EventoStatus;
import com.web.eventos.entities.Organizacao;
import com.web.eventos.security.CustomUserDetails;
import com.web.eventos.services.EventoService;
import com.web.eventos.services.LocalService;
import com.web.eventos.services.MidiaService;
import com.web.eventos.services.OrganizacaoService;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/eventos")
public class EventoController {
    private final EventoService eventoService;
    private final OrganizacaoService organizacaoService;
    private final LocalService localService;
    private final MidiaService midiaService;

    public EventoController(EventoService eventoService, OrganizacaoService organizacaoService,
            LocalService localService, MidiaService midiaService) {
        this.eventoService = eventoService;
        this.organizacaoService = organizacaoService;
        this.localService = localService;
        this.midiaService = midiaService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("midias");
    }

    @GetMapping
    @PreAuthorize("isAuthenticated() and principal.tipo == 'ORGANIZACAO'")
    public String listar(@AuthenticationPrincipal CustomUserDetails organizacaoLogada, Model model) {
        Organizacao organizacao = organizacaoService.findById(organizacaoLogada.getId());
        model.addAttribute("eventos", eventoService.findByOrganizacao(organizacao));
        return "eventos/listar";
    }

    @GetMapping("/buscar")
    public String buscar(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Categoria categoria,
            @RequestParam(required = false) Integer localId,
            @RequestParam(required = false) LocalDate dataInicio,
            Model model) {
        List<Evento> eventos = eventoService.buscar(query, categoria, localId, dataInicio);

        model.addAttribute("eventos", eventos);
        model.addAttribute("locais", localService.findAll());
        model.addAttribute("categorias", Categoria.values());
        model.addAttribute("query", query);
        model.addAttribute("categoriaSelecionada", categoria);
        model.addAttribute("localSelecionado", localId);
        model.addAttribute("dataInicioSelecionada", dataInicio);

        return "eventos/busca";
    }

    @GetMapping("/cadastrar")
    @PreAuthorize("isAuthenticated() and principal.tipo == 'ORGANIZACAO'")
    public String cadastrar(@AuthenticationPrincipal CustomUserDetails organizacaoLogada, Model model) {
        Organizacao organizacao = organizacaoService.findById(organizacaoLogada.getId());
        model.addAttribute("evento", new Evento());
        model.addAttribute("locais", localService.findByOrganizacao(organizacao));
        model.addAttribute("categorias", Categoria.values());
        model.addAttribute("statusList", EventoStatus.values());
        return "eventos/cadastrar";
    }

    @PostMapping("/cadastrar")
    @PreAuthorize("isAuthenticated() and principal.tipo == 'ORGANIZACAO'")
    public String salvar(@AuthenticationPrincipal CustomUserDetails organizacaoLogada,
            @ModelAttribute @Valid Evento evento, BindingResult result, RedirectAttributes redirectAttributes,
            Model model, @RequestParam(value = "midias", required = false) MultipartFile[] midias) {
        if (result.hasErrors()) {
            return "eventos/cadastrar";
        }

        try {
            Organizacao organizacao = organizacaoService.findById(organizacaoLogada.getId());
            evento.setOrganizacao(organizacao);
            Evento eventoSalvo = eventoService.salvar(evento);

            // Processar mídias se houver
            if (midias != null && midias.length > 0) {
                for (MultipartFile midia : midias) {
                    if (!midia.isEmpty()) {
                        midiaService.uploadArquivo(midia, eventoSalvo);
                    }
                }
            }

            redirectAttributes.addFlashAttribute("mensagem", "Evento cadastrado com sucesso!");
            return "redirect:/eventos";
        } catch (Exception e) {
            model.addAttribute("erro", "Ocorreu um erro ao cadastrar o evento. Tente novamente.");
            e.printStackTrace();
            return "eventos/cadastrar";
        }
    }

    @GetMapping("/editar/{id}")
    @PreAuthorize("isAuthenticated() and principal.tipo == 'ORGANIZACAO'")
    public String editar(@AuthenticationPrincipal CustomUserDetails organizacaoLogada, @PathVariable Integer id,
            Model model) {
        Evento evento = eventoService.findById(id);
        if (evento == null) {
            model.addAttribute("erro", "Evento não encontrado.");
            return "eventos/listar";
        }
        if (!evento.getOrganizacao().getId().equals(organizacaoLogada.getId())) {
            model.addAttribute("erro", "Você não tem permissão para editar este evento.");
            return "eventos/listar";
        }
        model.addAttribute("evento", evento);
        model.addAttribute("locais", localService.findByOrganizacao(evento.getOrganizacao()));
        model.addAttribute("categorias", Categoria.values());
        model.addAttribute("statusList", EventoStatus.values());
        return "eventos/editar";
    }

    @PostMapping("/editar/{id}")
    @PreAuthorize("isAuthenticated() and principal.tipo == 'ORGANIZACAO'")
    public String atualizar(@AuthenticationPrincipal CustomUserDetails organizacaoLogada, @PathVariable Integer id,
            @ModelAttribute @Valid Evento evento, BindingResult result, RedirectAttributes redirectAttributes,
            Model model, @RequestParam(value = "midias", required = false) MultipartFile[] midias) {
        if (result.hasErrors()) {
            return "eventos/editar";
        }

        Evento existing = eventoService.findById(id);
        if (existing == null || !existing.getOrganizacao().getId().equals(organizacaoLogada.getId())) {
            model.addAttribute("erro", "Evento não encontrado ou você não tem permissão.");
            return "eventos/editar";
        }

        try {
            evento.setId(id);
            Evento eventoAtualizado = eventoService.atualizar(evento);

            // Processar mídias se houver
            if (midias != null && midias.length > 0) {
                for (MultipartFile midia : midias) {
                    if (!midia.isEmpty()) {
                        midiaService.uploadArquivo(midia, eventoAtualizado);
                    }
                }
            }

            redirectAttributes.addFlashAttribute("mensagem", "Evento atualizado com sucesso!");
            return "redirect:/eventos";
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            return "eventos/editar";
        } catch (Exception e) {
            model.addAttribute("erro", "Ocorreu um erro ao atualizar o evento. Tente novamente.");
            e.printStackTrace();
            return "eventos/editar";
        }
    }

    @GetMapping("/excluir/{id}")
    @PreAuthorize("isAuthenticated() and principal.tipo == 'ORGANIZACAO'")
    public String excluir(@AuthenticationPrincipal CustomUserDetails organizacaoLogada, @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {
        Evento evento = eventoService.findById(id);

        if (evento != null && evento.getOrganizacao().getId().equals(organizacaoLogada.getId())) {
            eventoService.excluir(id);
        }

        redirectAttributes.addFlashAttribute("mensagem", "Evento excluído com sucesso!");
        return "redirect:/eventos";
    }
}
