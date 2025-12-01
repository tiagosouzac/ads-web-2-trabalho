package com.web.eventos.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import com.web.eventos.entities.Categoria;
import com.web.eventos.entities.Evento;
import com.web.eventos.entities.EventoStatus;
import com.web.eventos.entities.Comentario;
import com.web.eventos.entities.Organizacao;
import com.web.eventos.security.CustomUserDetails;
import com.web.eventos.services.AutenticacaoService;
import com.web.eventos.services.EventoService;
import com.web.eventos.services.LocalService;
import com.web.eventos.services.MidiaService;
import com.web.eventos.services.OrganizacaoService;
import com.web.eventos.services.InteressadoService;
import com.web.eventos.services.ComentarioService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final InteressadoService interessadoService;
    private final AutenticacaoService autenticacaoService;
    private final ComentarioService comentarioService;

    public EventoController(EventoService eventoService, OrganizacaoService organizacaoService,
            LocalService localService, MidiaService midiaService, InteressadoService interessadoService,
            AutenticacaoService autenticacaoService, ComentarioService comentarioService) {
        this.eventoService = eventoService;
        this.organizacaoService = organizacaoService;
        this.localService = localService;
        this.midiaService = midiaService;
        this.interessadoService = interessadoService;
        this.autenticacaoService = autenticacaoService;
        this.comentarioService = comentarioService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("midias");
    }

    @GetMapping
    @PreAuthorize("isAuthenticated() and principal.tipo == 'ORGANIZACAO'")
    public String listar(@AuthenticationPrincipal CustomUserDetails organizacaoLogada, Model model) {
        Organizacao organizacao = organizacaoService.findById(organizacaoLogada.getId());
        var eventos = eventoService.findByOrganizacao(organizacao);

        // Calcular interessadosCount para cada evento
        Map<Integer, Long> interessadosCountMap = new HashMap<>();
        for (var evento : eventos) {
            Long count = interessadoService.countInteressadosByEventoId(evento.getId());
            interessadosCountMap.put(evento.getId(), count);
        }

        model.addAttribute("eventos", eventos);
        model.addAttribute("interessadosCountMap", interessadosCountMap);
        return "eventos/listar";
    }

    @GetMapping("/buscar")
    public String buscar(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Categoria categoria,
            @RequestParam(required = false) String cidade,
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Evento> eventosPage = eventoService.buscar(query, categoria, cidade, dataInicio, pageable);

        // Mapear cada evento com seu respectivo interessadosCount
        Map<Integer, Long> interessadosCountMap = new java.util.HashMap<>();
        for (Evento evento : eventosPage.getContent()) {
            Long count = interessadoService.countInteressadosByEventoId(evento.getId());
            interessadosCountMap.put(evento.getId(), count);
        }

        model.addAttribute("eventosPage", eventosPage);
        model.addAttribute("interessadosCountMap", interessadosCountMap);
        model.addAttribute("categorias", Categoria.values());
        model.addAttribute("query", query);
        model.addAttribute("categoriaSelecionada", categoria != null ? categoria.getDisplayName() : null);
        model.addAttribute("cidadeSelecionada", cidade);
        model.addAttribute("dataInicioSelecionada", dataInicio);

        return "eventos/busca";
    }

    @GetMapping("/{id}")
    public String detalhes(@PathVariable Integer id,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page, Model model,
            HttpServletRequest request) {
        request.getSession(true);

        Evento evento = eventoService.findById(id);

        if (evento == null) {
            return "redirect:/erros/404";
        }

        model.addAttribute("evento", evento);

        CustomUserDetails user = autenticacaoService.getUsuarioAutenticado();

        Long interessadosCount = interessadoService.countInteressadosByEventoId(evento.getId());
        model.addAttribute("interessadosCount", interessadosCount);

        if (user != null && "USUARIO".equals(user.getTipo())) {
            boolean isInteressado = interessadoService.isInteressado(user.getId(),
                    evento.getId());
            model.addAttribute("isInteressado", isInteressado);
        } else {
            model.addAttribute("isInteressado", false);
        }

        Integer usuarioId = (user != null && "USUARIO".equals(user.getTipo())) ? user.getId() : null;

        int limit = 3;
        if (page > 0) {
            limit = 3 + page * 10;
        }

        List<Comentario> comentarios = comentarioService.getComentariosPriorizados(id, usuarioId, limit);

        long totalComentarios = comentarioService.getTotalComentarios(id);

        model.addAttribute("comentarios", comentarios);
        model.addAttribute("totalComentarios", totalComentarios);
        model.addAttribute("currentPage", page);
        model.addAttribute("isUsuario", user != null && "USUARIO".equals(user.getTipo()));

        // Buscar eventos recomendados na mesma categoria, excluindo o atual
        List<Evento> eventosRecomendados = eventoService.findByCategoriaExcluding(evento.getCategoria(), evento.getId(),
                4);

        // Calcular interessadosCount para os recomendados
        Map<Integer, Long> interessadosCountMap = new HashMap<>();
        for (Evento rec : eventosRecomendados) {
            Long count = interessadoService.countInteressadosByEventoId(rec.getId());
            interessadosCountMap.put(rec.getId(), count);
        }

        model.addAttribute("eventosRecomendados", eventosRecomendados);
        model.addAttribute("interessadosCountMap", interessadosCountMap);

        return "eventos/detalhes";
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
            Model model, @RequestParam(value = "midias", required = false) MultipartFile[] midias,
            @RequestParam(value = "midiasParaExcluir", required = false) String midiasParaExcluirStr) {
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

            // Processar exclusão de mídias se houver
            if (midiasParaExcluirStr != null && !midiasParaExcluirStr.isEmpty()) {
                String[] idsStr = midiasParaExcluirStr.split(",");
                for (String idStr : idsStr) {
                    try {
                        Integer midiaId = Integer.parseInt(idStr.trim());
                        midiaService.removerMidia(midiaId);
                    } catch (NumberFormatException e) {
                        // Ignorar IDs inválidos
                    }
                }
            }

            // Processar upload de novas mídias se houver
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
