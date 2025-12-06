package com.web.eventos.unit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.web.eventos.config.WithMockCustomUser;
import com.web.eventos.controllers.OrganizacaoController;
import com.web.eventos.entities.Organizacao;
import com.web.eventos.services.AutenticacaoService;
import com.web.eventos.services.LocalService;
import com.web.eventos.services.OrganizacaoService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(OrganizacaoController.class)
@DisplayName("Testes do OrganizacaoController")
@WithMockCustomUser
public class OrganizacaoControllerTest {
        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private OrganizacaoService organizacaoService;

        @MockitoBean
        private AutenticacaoService autenticacaoService;

        @MockitoBean
        private LocalService localService;

        @Test
        @DisplayName("Deve retornar a view de cadastrar")
        void deveRetornarViewCadastrar() throws Exception {
                mockMvc.perform(get("/organizacoes/cadastrar"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("organizacoes/cadastrar"))
                                .andExpect(model().attributeExists("organizacao"));
        }

        @Test
        @DisplayName("Deve salvar organização com sucesso")
        void deveSalvarOrganizacaoComSucesso() throws Exception {
                MockMultipartFile logo = new MockMultipartFile("logo", "logo.jpg", "image/jpeg",
                                "fake image".getBytes());

                when(organizacaoService.salvar(any(Organizacao.class), any())).thenReturn(new Organizacao());

                mockMvc.perform(multipart("/organizacoes/cadastrar")
                                .file(logo)
                                .param("nome", "Empresa XYZ")
                                .param("tipo", "PJ")
                                .param("cnpj", "12.345.678/0001-23")
                                .param("email", "empresa@example.com")
                                .param("telefone", "(11) 99999-9999")
                                .param("endereco", "Rua A, 123")
                                .param("senha", "senha123")
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(view().name("entrar"))
                                .andExpect(model().attributeExists("mensagem"))
                                .andExpect(model().attribute("email", "empresa@example.com"));
        }

        @Test
        @DisplayName("Deve retornar erro de validação no cadastro")
        void deveRetornarErroValidacaoCadastro() throws Exception {
                mockMvc.perform(post("/organizacoes/cadastrar")
                                .param("nome", "")
                                .param("tipo", "")
                                .param("cnpj", "123")
                                .param("email", "invalid")
                                .param("telefone", "")
                                .param("endereco", "")
                                .param("senha", "123")
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(view().name("organizacoes/cadastrar"));
        }

        @Test
        @DisplayName("Deve retornar erro de e-mail duplicado no cadastro")
        void deveRetornarErroEmailDuplicadoCadastro() throws Exception {
                doThrow(new IllegalArgumentException("E-mail já cadastrado")).when(organizacaoService)
                                .salvar(any(Organizacao.class), any());

                mockMvc.perform(multipart("/organizacoes/cadastrar")
                                .param("nome", "Empresa XYZ")
                                .param("tipo", "PJ")
                                .param("cnpj", "12.345.678/0001-23")
                                .param("email", "empresa@example.com")
                                .param("telefone", "(11) 99999-9999")
                                .param("endereco", "Rua A, 123")
                                .param("senha", "senha123")
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(view().name("organizacoes/cadastrar"));
        }

        @Test
        @DisplayName("Deve retornar a view de editar perfil")
        void deveRetornarViewEditarPerfil() throws Exception {
                Organizacao organizacao = new Organizacao();
                organizacao.setId(1);
                organizacao.setNome("Empresa XYZ");

                when(organizacaoService.findById(1)).thenReturn(organizacao);

                mockMvc.perform(get("/organizacoes/perfil/editar"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("organizacoes/editar"))
                                .andExpect(model().attributeExists("organizacao"));
        }

        @Test
        @DisplayName("Deve atualizar perfil com sucesso")
        void deveAtualizarPerfilComSucesso() throws Exception {
                Organizacao organizacaoAtualizada = new Organizacao();
                organizacaoAtualizada.setId(1);
                organizacaoAtualizada.setNome("Empresa XYZ Atualizada");

                when(organizacaoService.atualizar(any(Organizacao.class), any())).thenReturn(organizacaoAtualizada);
                doNothing().when(autenticacaoService).atualizarContextoAutenticacao(any(String.class));

                MockMultipartFile logo = new MockMultipartFile("logo", "logo.jpg", "image/jpeg",
                                "fake image".getBytes());

                mockMvc.perform(multipart("/organizacoes/perfil/editar")
                                .file(logo)
                                .param("nome", "Empresa XYZ Atualizada")
                                .param("tipo", "PJ")
                                .param("cnpj", "12.345.678/0001-23")
                                .param("email", "empresa@example.com")
                                .param("telefone", "(11) 99999-9999")
                                .param("endereco", "Rua A, 123")
                                .param("senha", "senha123")
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(view().name("organizacoes/editar"))
                                .andExpect(model().attributeExists("mensagem"))
                                .andExpect(model().attributeExists("organizacao"));
        }

        @Test
        @DisplayName("Deve excluir organização")
        void deveExcluirOrganizacao() throws Exception {
                doNothing().when(organizacaoService).excluir(1);

                mockMvc.perform(get("/organizacoes/excluir"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/logout"));
        }

        @Test
        @DisplayName("Deve tratar IOException ao salvar organização")
        void deveTratarIOExceptionAoSalvarOrganizacao() throws Exception {
                when(organizacaoService.salvar(any(Organizacao.class), any())).thenThrow(new IOException("Erro de IO"));

                mockMvc.perform(multipart("/organizacoes/cadastrar")
                                .param("nome", "Empresa XYZ")
                                .param("tipo", "PJ")
                                .param("cnpj", "12.345.678/0001-23")
                                .param("email", "empresa@example.com")
                                .param("telefone", "(11) 99999-9999")
                                .param("endereco", "Rua A, 123")
                                .param("senha", "senha123")
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(view().name("organizacoes/cadastrar"))
                                .andExpect(model().attribute("erro", "Erro ao fazer upload do logo: Erro de IO"));
        }

        @Test
        @DisplayName("Deve tratar exceção genérica ao salvar organização")
        void deveTratarExcecaoGenericaAoSalvarOrganizacao() throws Exception {
                when(organizacaoService.salvar(any(Organizacao.class), any()))
                                .thenThrow(new RuntimeException("Erro genérico"));

                mockMvc.perform(multipart("/organizacoes/cadastrar")
                                .param("nome", "Empresa XYZ")
                                .param("tipo", "PJ")
                                .param("cnpj", "12.345.678/0001-23")
                                .param("email", "empresa@example.com")
                                .param("telefone", "(11) 99999-9999")
                                .param("endereco", "Rua A, 123")
                                .param("senha", "senha123")
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(view().name("organizacoes/cadastrar"))
                                .andExpect(model().attribute("erro",
                                                "Ocorreu um erro ao processar o cadastro. Tente novamente."));
        }

        @Test
        @DisplayName("Deve tratar erros de validação ao atualizar organização")
        void deveTratarErrosDeValidacaoAoAtualizarOrganizacao() throws Exception {
                mockMvc.perform(multipart("/organizacoes/perfil/editar")
                                .param("nome", "") // Inválido
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(view().name("organizacoes/editar"));
        }

        @Test
        @DisplayName("Deve tratar IllegalArgumentException ao atualizar organização")
        void deveTratarIllegalArgumentExceptionAoAtualizarOrganizacao() throws Exception {
                when(organizacaoService.atualizar(any(Organizacao.class), any()))
                                .thenThrow(new IllegalArgumentException("Erro de argumento"));

                mockMvc.perform(multipart("/organizacoes/perfil/editar")
                                .param("nome", "Empresa XYZ Atualizada")
                                .param("tipo", "PJ")
                                .param("cnpj", "12.345.678/0001-23")
                                .param("email", "empresa@example.com")
                                .param("telefone", "(11) 99999-9999")
                                .param("endereco", "Rua A, 123")
                                .param("senha", "senha123")
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(view().name("organizacoes/editar"))
                                .andExpect(model().attribute("erro", "Erro de argumento"));
        }

        @Test
        @DisplayName("Deve tratar IOException ao atualizar organização")
        void deveTratarIOExceptionAoAtualizarOrganizacao() throws Exception {
                when(organizacaoService.atualizar(any(Organizacao.class), any()))
                                .thenThrow(new IOException("Erro de IO"));

                mockMvc.perform(multipart("/organizacoes/perfil/editar")
                                .param("nome", "Empresa XYZ Atualizada")
                                .param("tipo", "PJ")
                                .param("cnpj", "12.345.678/0001-23")
                                .param("email", "empresa@example.com")
                                .param("telefone", "(11) 99999-9999")
                                .param("endereco", "Rua A, 123")
                                .param("senha", "senha123")
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(view().name("organizacoes/editar"))
                                .andExpect(model().attribute("erro", "Erro ao fazer upload do logo: Erro de IO"));
        }

        @Test
        @DisplayName("Deve tratar exceção genérica ao atualizar organização")
        void deveTratarExcecaoGenericaAoAtualizarOrganizacao() throws Exception {
                when(organizacaoService.atualizar(any(Organizacao.class), any()))
                                .thenThrow(new RuntimeException("Erro genérico"));

                mockMvc.perform(multipart("/organizacoes/perfil/editar")
                                .param("nome", "Empresa XYZ Atualizada")
                                .param("tipo", "PJ")
                                .param("cnpj", "12.345.678/0001-23")
                                .param("email", "empresa@example.com")
                                .param("telefone", "(11) 99999-9999")
                                .param("endereco", "Rua A, 123")
                                .param("senha", "senha123")
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(view().name("organizacoes/editar"))
                                .andExpect(model().attribute("erro",
                                                "Ocorreu um erro ao atualizar o perfil. Tente novamente."));
        }
}