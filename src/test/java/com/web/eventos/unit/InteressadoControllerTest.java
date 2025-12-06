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
import com.web.eventos.controllers.InteressadoController;
import com.web.eventos.services.InteressadoService;
import com.web.eventos.services.LocalService;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(InteressadoController.class)
@DisplayName("Testes do InteressadoController")
@WithMockCustomUser
public class InteressadoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InteressadoService interessadoService;

    @MockitoBean
    private LocalService localService;

    @Test
    @DisplayName("Deve salvar interesse com sucesso")
    void deveSalvarInteresseComSucesso() throws Exception {
        doNothing().when(interessadoService).salvarInteresse(1, 1);

        mockMvc.perform(post("/interessados/salvar")
                .param("eventoId", "1")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/eventos/1"))
                .andExpect(flash().attribute("success", "Interesse registrado com sucesso!"));
    }

    @Test
    @DisplayName("Deve retornar erro ao salvar interesse")
    void deveRetornarErroAoSalvarInteresse() throws Exception {
        doThrow(new IllegalArgumentException("Erro específico")).when(interessadoService).salvarInteresse(1, 1);

        mockMvc.perform(post("/interessados/salvar")
                .param("eventoId", "1")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/eventos/1"))
                .andExpect(flash().attribute("error", "Erro específico"));
    }

    @Test
    @DisplayName("Deve excluir interesse com sucesso")
    void deveExcluirInteresseComSucesso() throws Exception {
        doNothing().when(interessadoService).excluirInteresse(1, 1);

        mockMvc.perform(post("/interessados/excluir")
                .param("eventoId", "1")
                .param("_method", "DELETE")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/eventos/1"))
                .andExpect(flash().attribute("success", "Interesse removido com sucesso!"));
    }

    @Test
    @DisplayName("Deve retornar erro ao excluir interesse")
    void deveRetornarErroAoExcluirInteresse() throws Exception {
        doThrow(new IllegalArgumentException("Erro específico")).when(interessadoService).excluirInteresse(1, 1);

        mockMvc.perform(post("/interessados/excluir")
                .param("eventoId", "1")
                .param("_method", "DELETE")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/eventos/1"))
                .andExpect(flash().attribute("error", "Erro específico"));
    }

    @Test
    @DisplayName("Deve redirecionar se método não for DELETE")
    void deveRedirecionarSeMetodoNaoDelete() throws Exception {
        mockMvc.perform(post("/interessados/excluir")
                .param("eventoId", "1")
                .param("_method", "POST")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/eventos/1"));
    }

    @Test
    @DisplayName("Deve redirecionar se usuário não for do tipo USUARIO ao salvar")
    @WithMockCustomUser(tipo = "ADMIN")
    void deveRedirecionarSeUsuarioNaoForDoTipoUsuarioAoSalvar() throws Exception {
        mockMvc.perform(post("/interessados/salvar")
                .param("eventoId", "1")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/entrar"));
    }

    @Test
    @DisplayName("Deve tratar exceção genérica ao salvar interesse")
    void deveTratarExcecaoGenericaAoSalvarInteresse() throws Exception {
        doThrow(new RuntimeException("Erro genérico")).when(interessadoService).salvarInteresse(1, 1);

        mockMvc.perform(post("/interessados/salvar")
                .param("eventoId", "1")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/eventos/1"))
                .andExpect(flash().attribute("error", "Erro ao registrar interesse."));
    }

    @Test
    @DisplayName("Deve redirecionar se usuário não for do tipo USUARIO ao excluir")
    @WithMockCustomUser(tipo = "ADMIN")
    void deveRedirecionarSeUsuarioNaoForDoTipoUsuarioAoExcluir() throws Exception {
        mockMvc.perform(post("/interessados/excluir")
                .param("eventoId", "1")
                .param("_method", "DELETE")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/entrar"));
    }

    @Test
    @DisplayName("Deve tratar exceção genérica ao excluir interesse")
    void deveTratarExcecaoGenericaAoExcluirInteresse() throws Exception {
        doThrow(new RuntimeException("Erro genérico")).when(interessadoService).excluirInteresse(1, 1);

        mockMvc.perform(post("/interessados/excluir")
                .param("eventoId", "1")
                .param("_method", "DELETE")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/eventos/1"))
                .andExpect(flash().attribute("error", "Erro ao remover interesse."));
    }
}