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
import com.web.eventos.controllers.UsuarioController;
import com.web.eventos.entities.Usuario;
import com.web.eventos.services.AutenticacaoService;
import com.web.eventos.services.LocalService;
import com.web.eventos.services.UsuarioService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(UsuarioController.class)
@DisplayName("Testes do UsuarioController")
@WithMockCustomUser
public class UsuarioControllerTest {
        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private UsuarioService usuarioService;

        @MockitoBean
        private AutenticacaoService autenticacaoService;

        @MockitoBean
        private LocalService localService;

        @Test
        @DisplayName("Deve retornar a view de cadastrar")
        void deveRetornarViewCadastrar() throws Exception {
                mockMvc.perform(get("/usuarios/cadastrar"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("usuarios/cadastrar"))
                                .andExpect(model().attributeExists("usuario"));
        }

        @Test
        @DisplayName("Deve salvar usuário com sucesso")
        void deveSalvarUsuarioComSucesso() throws Exception {
                MockMultipartFile avatar = new MockMultipartFile("avatar", "avatar.jpg", "image/jpeg",
                                "fake image".getBytes());

                when(usuarioService.salvar(any(Usuario.class), any())).thenReturn(new Usuario());

                mockMvc.perform(multipart("/usuarios/cadastrar")
                                .file(avatar)
                                .param("nome", "João Silva")
                                .param("email", "joao@example.com")
                                .param("cpf", "12345678901")
                                .param("senha", "senha123")
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(view().name("entrar"))
                                .andExpect(model().attributeExists("mensagem"))
                                .andExpect(model().attribute("email", "joao@example.com"));
        }

        @Test
        @DisplayName("Deve retornar erro de validação no cadastro")
        void deveRetornarErroValidacaoCadastro() throws Exception {
                mockMvc.perform(post("/usuarios/cadastrar")
                                .param("nome", "")
                                .param("email", "invalid")
                                .param("cpf", "123")
                                .param("senha", "123")
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(view().name("usuarios/cadastrar"));
        }

        @Test
        @DisplayName("Deve retornar erro de e-mail duplicado no cadastro")
        void deveRetornarErroEmailDuplicadoCadastro() throws Exception {
                doThrow(new IllegalArgumentException("E-mail já cadastrado")).when(usuarioService).salvar(
                                any(Usuario.class),
                                any());

                mockMvc.perform(multipart("/usuarios/cadastrar")
                                .param("nome", "João Silva")
                                .param("email", "joao@example.com")
                                .param("cpf", "12345678901")
                                .param("senha", "senha123")
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(view().name("usuarios/cadastrar"));
        }

        @Test
        @DisplayName("Deve retornar a view de editar perfil")
        void deveRetornarViewEditarPerfil() throws Exception {
                Usuario usuario = new Usuario();
                usuario.setId(1);
                usuario.setNome("João Silva");

                when(usuarioService.findById(1)).thenReturn(usuario);

                mockMvc.perform(get("/usuarios/perfil/editar"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("usuarios/editar"))
                                .andExpect(model().attributeExists("usuario"));
        }

        @Test
        @DisplayName("Deve atualizar perfil com sucesso")
        void deveAtualizarPerfilComSucesso() throws Exception {
                Usuario usuarioAtualizado = new Usuario();
                usuarioAtualizado.setId(1);
                usuarioAtualizado.setNome("João Silva Atualizado");

                when(usuarioService.atualizar(any(Usuario.class), any())).thenReturn(usuarioAtualizado);
                doNothing().when(autenticacaoService).atualizarContextoAutenticacao(any(String.class));

                MockMultipartFile avatar = new MockMultipartFile("avatar", "avatar.jpg", "image/jpeg",
                                "fake image".getBytes());

                mockMvc.perform(multipart("/usuarios/perfil/editar")
                                .file(avatar)
                                .param("nome", "João Silva Atualizado")
                                .param("email", "joao@example.com")
                                .param("cpf", "12345678901")
                                .param("senha", "senha123")
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(view().name("usuarios/editar"))
                                .andExpect(model().attributeExists("mensagem"))
                                .andExpect(model().attributeExists("usuario"));
        }

        @Test
        @DisplayName("Deve excluir usuário")
        void deveExcluirUsuario() throws Exception {
                doNothing().when(usuarioService).excluir(1);

                mockMvc.perform(get("/usuarios/excluir"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/logout"));
        }

        @Test
        @DisplayName("Deve tratar IOException ao salvar usuário")
        void deveTratarIOExceptionAoSalvarUsuario() throws Exception {
                when(usuarioService.salvar(any(Usuario.class), any())).thenThrow(new IOException("Erro de IO"));

                mockMvc.perform(multipart("/usuarios/cadastrar")
                                .param("nome", "João Silva")
                                .param("email", "joao@example.com")
                                .param("cpf", "12345678901")
                                .param("senha", "senha123")
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(view().name("usuarios/cadastrar"))
                                .andExpect(model().attribute("erro", "Erro ao fazer upload do avatar: Erro de IO"));
        }

        @Test
        @DisplayName("Deve tratar exceção genérica ao salvar usuário")
        void deveTratarExcecaoGenericaAoSalvarUsuario() throws Exception {
                when(usuarioService.salvar(any(Usuario.class), any())).thenThrow(new RuntimeException("Erro genérico"));

                mockMvc.perform(multipart("/usuarios/cadastrar")
                                .param("nome", "João Silva")
                                .param("email", "joao@example.com")
                                .param("cpf", "12345678901")
                                .param("senha", "senha123")
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(view().name("usuarios/cadastrar"))
                                .andExpect(model().attribute("erro",
                                                "Ocorreu um erro ao processar o cadastro. Tente novamente."));
        }

        @Test
        @DisplayName("Deve tratar erros de validação ao atualizar usuário")
        void deveTratarErrosDeValidacaoAoAtualizarUsuario() throws Exception {
                mockMvc.perform(multipart("/usuarios/perfil/editar")
                                .param("nome", "") // Inválido
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(view().name("usuarios/editar"));
        }

        @Test
        @DisplayName("Deve tratar IllegalArgumentException ao atualizar usuário")
        void deveTratarIllegalArgumentExceptionAoAtualizarUsuario() throws Exception {
                when(usuarioService.atualizar(any(Usuario.class), any()))
                                .thenThrow(new IllegalArgumentException("Erro de argumento"));

                mockMvc.perform(multipart("/usuarios/perfil/editar")
                                .param("nome", "João Silva Atualizado")
                                .param("email", "joao@example.com")
                                .param("cpf", "12345678901")
                                .param("senha", "senha123")
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(view().name("usuarios/editar"))
                                .andExpect(model().attribute("erro", "Erro de argumento"));
        }

        @Test
        @DisplayName("Deve tratar IOException ao atualizar usuário")
        void deveTratarIOExceptionAoAtualizarUsuario() throws Exception {
                when(usuarioService.atualizar(any(Usuario.class), any())).thenThrow(new IOException("Erro de IO"));

                mockMvc.perform(multipart("/usuarios/perfil/editar")
                                .param("nome", "João Silva Atualizado")
                                .param("email", "joao@example.com")
                                .param("cpf", "12345678901")
                                .param("senha", "senha123")
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(view().name("usuarios/editar"))
                                .andExpect(model().attribute("erro", "Erro ao fazer upload do avatar: Erro de IO"));
        }

        @Test
        @DisplayName("Deve tratar exceção genérica ao atualizar usuário")
        void deveTratarExcecaoGenericaAoAtualizarUsuario() throws Exception {
                when(usuarioService.atualizar(any(Usuario.class), any()))
                                .thenThrow(new RuntimeException("Erro genérico"));

                mockMvc.perform(multipart("/usuarios/perfil/editar")
                                .param("nome", "João Silva Atualizado")
                                .param("email", "joao@example.com")
                                .param("cpf", "12345678901")
                                .param("senha", "senha123")
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(view().name("usuarios/editar"))
                                .andExpect(model().attribute("erro",
                                                "Ocorreu um erro ao atualizar o perfil. Tente novamente."));
        }
}