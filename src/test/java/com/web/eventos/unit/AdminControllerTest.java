package com.web.eventos.unit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.web.eventos.config.WithMockCustomUser;
import com.web.eventos.controllers.AdminController;
import com.web.eventos.entities.Banner;
import com.web.eventos.services.BannerService;
import com.web.eventos.services.LocalService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(AdminController.class)
@DisplayName("Testes do AdminController")
@WithMockCustomUser
public class AdminControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BannerService bannerService;

    @MockitoBean
    private LocalService localService;

    @Test
    @DisplayName("Deve listar banners")
    void deveListarBanners() throws Exception {
        when(bannerService.getBanners()).thenReturn(List.of());

        mockMvc.perform(get("/admin/banners"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/banners"))
                .andExpect(model().attributeExists("banners"));
    }

    @Test
    @DisplayName("Deve retornar a view de cadastrar banner")
    void deveRetornarViewCadastrarBanner() throws Exception {
        mockMvc.perform(get("/admin/banners/cadastrar"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/banners/cadastrar"));
    }

    @Test
    @DisplayName("Deve salvar banner com sucesso")
    void deveSalvarBannerComSucesso() throws Exception {
        MockMultipartFile imagem = new MockMultipartFile("imagem", "banner.jpg", "image/jpeg", "fake image".getBytes());

        when(bannerService.salvarComUpload(any(), anyString())).thenReturn(new Banner());

        mockMvc.perform(multipart("/admin/banners/cadastrar")
                .file(imagem)
                .param("titulo", "Banner Teste")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/banners"))
                .andExpect(flash().attribute("mensagem", "Banner adicionado com sucesso!"));
    }

    @Test
    @DisplayName("Deve excluir banner com sucesso")
    void deveExcluirBannerComSucesso() throws Exception {
        doNothing().when(bannerService).excluir(1);

        mockMvc.perform(post("/admin/banners/excluir/1")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/banners"))
                .andExpect(flash().attribute("mensagem", "Banner removido com sucesso!"));
    }

    @Test
    @DisplayName("Deve tratar exceção ao salvar banner")
    void deveTratarExcecaoAoSalvarBanner() throws Exception {
        MockMultipartFile imagem = new MockMultipartFile("imagem", "banner.jpg", "image/jpeg", "fake image".getBytes());

        when(bannerService.salvarComUpload(any(), anyString())).thenThrow(new RuntimeException("Erro genérico"));

        mockMvc.perform(multipart("/admin/banners/cadastrar")
                .file(imagem)
                .param("titulo", "Banner Teste")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/banners"))
                .andExpect(flash().attribute("erro", "Erro ao adicionar banner: Erro genérico"));
    }

    @Test
    @DisplayName("Deve tratar exceção ao excluir banner")
    void deveTratarExcecaoAoExcluirBanner() throws Exception {
        doThrow(new RuntimeException("Erro genérico")).when(bannerService).excluir(1);

        mockMvc.perform(post("/admin/banners/excluir/1")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/banners"))
                .andExpect(flash().attribute("erro", "Erro ao remover banner: Erro genérico"));
    }
}