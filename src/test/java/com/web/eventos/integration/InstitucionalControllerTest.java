package com.web.eventos.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class InstitucionalControllerTest extends BaseIntegrationTest {

    @Test
    @DisplayName("Deve exibir página sobre")
    void deveExibirPaginaSobre() throws Exception {
        mockMvc.perform(get("/sobre"))
                .andExpect(status().isOk())
                .andExpect(view().name("institucionais/sobre"));
    }

    @Test
    @DisplayName("Deve exibir página termos de uso")
    void deveExibirPaginaTermosDeUso() throws Exception {
        mockMvc.perform(get("/termos-de-uso"))
                .andExpect(status().isOk())
                .andExpect(view().name("institucionais/termos-de-uso"));
    }

    @Test
    @DisplayName("Deve exibir página política de privacidade")
    void deveExibirPaginaPoliticaDePrivacidade() throws Exception {
        mockMvc.perform(get("/politica-de-privacidade"))
                .andExpect(status().isOk())
                .andExpect(view().name("institucionais/politica-de-privacidade"));
    }
}
