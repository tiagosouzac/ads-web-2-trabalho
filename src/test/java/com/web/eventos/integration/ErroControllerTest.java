package com.web.eventos.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ErroControllerTest extends BaseIntegrationTest {

    @Test
    @DisplayName("Deve exibir página de acesso negado")
    void deveExibirPaginaAcessoNegado() throws Exception {
        mockMvc.perform(get("/erros/acesso-negado"))
                .andExpect(status().isOk())
                .andExpect(view().name("erros/acesso-negado"));
    }

    @Test
    @DisplayName("Deve exibir página 404")
    void deveExibirPagina404() throws Exception {
        mockMvc.perform(get("/erros/404"))
                .andExpect(status().isOk())
                .andExpect(view().name("erros/404"));
    }
}
