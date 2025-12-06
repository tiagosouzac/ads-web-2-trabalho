package com.web.eventos.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.web.eventos.entities.Banner;
import com.web.eventos.repositories.BannerRepository;
import com.web.eventos.security.CustomUserDetails;

public class AdminControllerTest extends BaseIntegrationTest {

    @Autowired
    private BannerRepository bannerRepository;

    private CustomUserDetails getAdminUser() {
        return new CustomUserDetails(
                1,
                "admin@email.com",
                "senha",
                "Admin",
                null,
                "ADMIN",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("Deve listar banners para admin")
    void deveListarBanners() throws Exception {
        mockMvc.perform(get("/admin/banners")
                .with(user(getAdminUser())))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/banners"))
                .andExpect(model().attributeExists("banners"));
    }

    @Test
    @DisplayName("Deve exibir formulário de cadastro de banner")
    void deveExibirFormularioCadastro() throws Exception {
        mockMvc.perform(get("/admin/banners/cadastrar")
                .with(user(getAdminUser())))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/banners/cadastrar"));
    }

    @Test
    @DisplayName("Deve cadastrar banner com sucesso")
    void deveCadastrarBanner() throws Exception {
        MockMultipartFile imagem = new MockMultipartFile(
                "imagem",
                "banner.jpg",
                "image/jpeg",
                "conteudo".getBytes());

        mockMvc.perform(multipart("/admin/banners/cadastrar")
                .file(imagem)
                .param("titulo", "Novo Banner")
                .with(user(getAdminUser()))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/banners"))
                .andExpect(flash().attributeExists("mensagem"));
    }

    @Test
    @DisplayName("Deve excluir banner com sucesso")
    void deveExcluirBanner() throws Exception {
        Banner banner = new Banner();
        banner.setTitulo("Banner Teste");
        banner.setUrl("http://url.com/banner.jpg");
        banner = bannerRepository.save(banner);

        mockMvc.perform(post("/admin/banners/excluir/" + banner.getId())
                .with(user(getAdminUser()))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/banners"))
                .andExpect(flash().attributeExists("mensagem"));
    }
}
