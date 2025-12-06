package com.web.eventos.unit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.web.eventos.config.WithMockCustomUser;
import com.web.eventos.controllers.ComentarioController;
import com.web.eventos.entities.Comentario;
import com.web.eventos.entities.Evento;
import com.web.eventos.entities.Usuario;
import com.web.eventos.services.ComentarioService;
import com.web.eventos.services.EventoService;
import com.web.eventos.services.LocalService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(ComentarioController.class)
@DisplayName("Testes do ComentarioController")
@WithMockCustomUser
public class ComentarioControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ComentarioService comentarioService;

    @MockitoBean
    private EventoService eventoService;

    @MockitoBean
    private LocalService localService;

    @Test
    @DisplayName("Deve salvar comentário com sucesso")
    void deveSalvarComentarioComSucesso() throws Exception {
        Evento evento = new Evento();
        evento.setId(1);

        when(eventoService.findById(1)).thenReturn(evento);
        when(comentarioService.salvar(any(Comentario.class))).thenReturn(new Comentario());

        mockMvc.perform(post("/comentarios/salvar")
                .param("eventoId", "1")
                .param("comentario", "Ótimo evento!")
                .param("nota", "5")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/eventos/1"));
    }

    @Test
    @DisplayName("Deve salvar comentário sem nota")
    void deveSalvarComentarioSemNota() throws Exception {
        Evento evento = new Evento();
        evento.setId(1);

        when(eventoService.findById(1)).thenReturn(evento);
        when(comentarioService.salvar(any(Comentario.class))).thenReturn(new Comentario());

        mockMvc.perform(post("/comentarios/salvar")
                .param("eventoId", "1")
                .param("comentario", "Bom evento!")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/eventos/1"));
    }

    @Test
    @DisplayName("Deve retornar erro se evento não encontrado")
    void deveRetornarErroEventoNaoEncontrado() throws Exception {
        when(eventoService.findById(1)).thenReturn(null);

        mockMvc.perform(post("/comentarios/salvar")
                .param("eventoId", "1")
                .param("comentario", "Comentário")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/erros/404"));
    }

    @Test
    @DisplayName("Deve excluir comentário com sucesso")
    void deveExcluirComentarioComSucesso() throws Exception {
        Evento evento = new Evento();
        evento.setId(1);

        Usuario usuario = new Usuario();
        usuario.setId(1);

        Comentario comentario = new Comentario();
        comentario.setId(1);
        comentario.setEvento(evento);
        comentario.setUsuario(usuario);

        when(comentarioService.findById(1)).thenReturn(comentario);
        doNothing().when(comentarioService).excluir(1);

        mockMvc.perform(post("/comentarios/1/excluir")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/eventos/1"))
                .andExpect(flash().attribute("success", "Comentário excluído com sucesso."));
    }

    @Test
    @DisplayName("Deve retornar erro se comentário não encontrado")
    void deveRetornarErroComentarioNaoEncontrado() throws Exception {
        when(comentarioService.findById(1)).thenReturn(null);

        mockMvc.perform(post("/comentarios/1/excluir")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/eventos"))
                .andExpect(flash().attribute("error", "Comentário não encontrado."));
    }

    @Test
    @DisplayName("Deve retornar erro se usuário não é dono do comentário")
    void deveRetornarErroUsuarioNaoDono() throws Exception {
        Evento evento = new Evento();
        evento.setId(1);

        Usuario usuario = new Usuario();
        usuario.setId(2); // different id

        Comentario comentario = new Comentario();
        comentario.setId(1);
        comentario.setEvento(evento);
        comentario.setUsuario(usuario);

        when(comentarioService.findById(1)).thenReturn(comentario);

        mockMvc.perform(post("/comentarios/1/excluir")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/eventos/1"))
                .andExpect(flash().attribute("error", "Você não pode excluir este comentário."));
    }

    @Test
    @DisplayName("Deve redirecionar se usuário não for do tipo USUARIO ao comentar")
    @WithMockCustomUser(tipo = "ADMIN")
    void deveRedirecionarSeUsuarioNaoForDoTipoUsuarioAoComentar() throws Exception {
        mockMvc.perform(post("/comentarios/salvar")
                .param("eventoId", "1")
                .param("comentario", "Comentário")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/eventos/1"));
    }

    @Test
    @DisplayName("Deve redirecionar se usuário não for do tipo USUARIO ao excluir")
    @WithMockCustomUser(tipo = "ADMIN")
    void deveRedirecionarSeUsuarioNaoForDoTipoUsuarioAoExcluir() throws Exception {
        mockMvc.perform(post("/comentarios/1/excluir")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/eventos"));
    }
}