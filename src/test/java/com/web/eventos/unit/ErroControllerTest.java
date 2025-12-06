package com.web.eventos.unit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.web.eventos.config.WithMockCustomUser;
import com.web.eventos.controllers.ErroController;
import com.web.eventos.services.LocalService;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(ErroController.class)
@DisplayName("Testes do ErroController")
@WithMockCustomUser
public class ErroControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LocalService localService;

    @Test
    @DisplayName("Deve retornar a view de acesso negado")
    void deveRetornarViewAcessoNegado() throws Exception {
        mockMvc.perform(get("/erros/acesso-negado"))
                .andExpect(status().isOk())
                .andExpect(view().name("erros/acesso-negado"));
    }

    @Test
    @DisplayName("Deve retornar a view de página não encontrada")
    void deveRetornarViewPaginaNaoEncontrada() throws Exception {
        mockMvc.perform(get("/erros/404"))
                .andExpect(status().isOk())
                .andExpect(view().name("erros/404"));
    }
}
