package com.web.eventos.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HomeControllerTest extends BaseIntegrationTest {

    @Test
    @DisplayName("Deve exibir página inicial com atributos")
    void deveExibirPaginaInicial() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("eventosPorCategoria"))
                .andExpect(model().attributeExists("interessadosCountMap"))
                .andExpect(model().attributeExists("banners"));
    }
}
