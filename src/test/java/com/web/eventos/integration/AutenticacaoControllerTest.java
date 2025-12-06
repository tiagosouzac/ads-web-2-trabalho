package com.web.eventos.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AutenticacaoControllerTest extends BaseIntegrationTest {

    @Test
    @DisplayName("Deve exibir página de login")
    void deveExibirPaginaLogin() throws Exception {
        mockMvc.perform(get("/entrar"))
                .andExpect(status().isOk())
                .andExpect(view().name("entrar"));
    }
}
