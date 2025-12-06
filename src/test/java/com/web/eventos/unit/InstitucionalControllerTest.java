package com.web.eventos.unit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.web.eventos.config.WithMockCustomUser;
import com.web.eventos.controllers.InstitucionalController;
import com.web.eventos.services.LocalService;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(InstitucionalController.class)
@DisplayName("Testes do InstitucionalController")
@WithMockCustomUser
public class InstitucionalControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LocalService localService;

    @Test
    @DisplayName("Deve retornar a view sobre")
    void deveRetornarViewSobre() throws Exception {
        mockMvc.perform(get("/sobre"))
                .andExpect(status().isOk())
                .andExpect(view().name("institucionais/sobre"));
    }

    @Test
    @DisplayName("Deve retornar a view termos de uso")
    void deveRetornarViewTermosDeUso() throws Exception {
        mockMvc.perform(get("/termos-de-uso"))
                .andExpect(status().isOk())
                .andExpect(view().name("institucionais/termos-de-uso"));
    }

    @Test
    @DisplayName("Deve retornar a view política de privacidade")
    void deveRetornarViewPoliticaDePrivacidade() throws Exception {
        mockMvc.perform(get("/politica-de-privacidade"))
                .andExpect(status().isOk())
                .andExpect(view().name("institucionais/politica-de-privacidade"));
    }
}