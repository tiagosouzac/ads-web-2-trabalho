package com.web.eventos.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.web.eventos.entities.Usuario;
import com.web.eventos.entities.UsuarioRole;
import com.web.eventos.repositories.UsuarioRepository;
import com.web.eventos.security.CustomUserDetails;

public class UsuarioControllerTest extends BaseIntegrationTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private CustomUserDetails getUsuarioUser(Usuario usuario) {
        return new CustomUserDetails(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getSenha(),
                usuario.getNome(),
                null,
                "USUARIO",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    @DisplayName("Deve exibir formulário de cadastro de usuário")
    void deveExibirFormularioCadastro() throws Exception {
        mockMvc.perform(get("/usuarios/cadastrar"))
                .andExpect(status().isOk())
                .andExpect(view().name("usuarios/cadastrar"))
                .andExpect(model().attributeExists("usuario"));
    }

    @Test
    @DisplayName("Deve cadastrar usuário com sucesso")
    void deveCadastrarUsuarioComSucesso() throws Exception {
        mockMvc.perform(multipart("/usuarios/cadastrar")
                .param("nome", "Novo Usuário")
                .param("email", "novo.usuario@email.com")
                .param("senha", "senha123")
                .param("cpf", "123.456.789-00")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("entrar"))
                .andExpect(model().attributeExists("mensagem"));
    }

    @Test
    @DisplayName("Deve falhar ao cadastrar usuário com email duplicado")
    void deveFalharAoCadastrarUsuarioComEmailDuplicado() throws Exception {
        // Primeiro cadastro
        mockMvc.perform(multipart("/usuarios/cadastrar")
                .param("nome", "Usuário 1")
                .param("email", "duplicado@email.com")
                .param("senha", "senha123")
                .param("cpf", "111.222.333-44")
                .with(csrf()))
                .andExpect(status().isOk());

        // Segundo cadastro com mesmo email
        mockMvc.perform(multipart("/usuarios/cadastrar")
                .param("nome", "Usuário 2")
                .param("email", "duplicado@email.com")
                .param("senha", "senha123")
                .param("cpf", "555.666.777-88")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("usuarios/cadastrar"))
                .andExpect(model().attributeHasFieldErrors("usuario", "email"));
    }

    @Test
    @DisplayName("Deve exibir formulário de edição de perfil")
    void deveExibirFormularioEdicao() throws Exception {
        Usuario usuario = Usuario.builder()
                .nome("Usuário Edição")
                .email("usuario.edicao@teste.com")
                .senha("senha123")
                .cpf("111.111.111-11")
                .role(UsuarioRole.USER)
                .build();
        usuarioRepository.save(usuario);

        mockMvc.perform(get("/usuarios/perfil/editar")
                .with(user(getUsuarioUser(usuario))))
                .andExpect(status().isOk())
                .andExpect(view().name("usuarios/editar"))
                .andExpect(model().attributeExists("usuario"));
    }

    @Test
    @DisplayName("Deve editar perfil com sucesso")
    void deveEditarPerfil() throws Exception {
        Usuario usuario = Usuario.builder()
                .nome("Usuário Para Editar")
                .email("usuario.editar@teste.com")
                .senha("senha123")
                .cpf("222.222.222-22")
                .role(UsuarioRole.USER)
                .build();
        usuarioRepository.save(usuario);

        mockMvc.perform(multipart("/usuarios/perfil/editar")
                .param("nome", "Usuário Editado")
                .param("email", "usuario.editado@teste.com")
                .param("senha", "senha123")
                .param("cpf", "222.222.222-22")
                .with(user(getUsuarioUser(usuario)))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("usuarios/editar"))
                .andExpect(model().attributeExists("mensagem"));
    }

    @Test
    @DisplayName("Deve excluir conta com sucesso")
    void deveExcluirConta() throws Exception {
        Usuario usuario = Usuario.builder()
                .nome("Usuário Para Excluir")
                .email("usuario.excluir@teste.com")
                .senha("senha123")
                .cpf("333.333.333-33")
                .role(UsuarioRole.USER)
                .build();
        usuarioRepository.save(usuario);

        mockMvc.perform(get("/usuarios/excluir")
                .with(user(getUsuarioUser(usuario))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/logout"));
    }
}
