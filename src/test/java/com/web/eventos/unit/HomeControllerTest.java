package com.web.eventos.unit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.web.eventos.config.WithMockCustomUser;
import com.web.eventos.controllers.HomeController;
import com.web.eventos.services.BannerService;
import com.web.eventos.services.EventoService;
import com.web.eventos.services.InteressadoService;
import com.web.eventos.services.LocalService;

import static org.mockito.Mockito.when;

@WebMvcTest(HomeController.class)
@DisplayName("Testes do HomeController")
@WithMockCustomUser
public class HomeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventoService eventoService;

    @MockitoBean
    private BannerService bannerService;

    @MockitoBean
    private InteressadoService interessadoService;

    @MockitoBean
    private LocalService localService;

    @Test
    @DisplayName("Deve retornar a view da página inicial")
    void deveRetornarViewHome() throws Exception {
        // Mock dos serviços
        when(eventoService.getEventosPorCategoria()).thenReturn(new HashMap<>());
        when(bannerService.getBanners()).thenReturn(List.of());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("eventosPorCategoria"))
                .andExpect(model().attributeExists("interessadosCountMap"))
                .andExpect(model().attributeExists("banners"));
    }
}