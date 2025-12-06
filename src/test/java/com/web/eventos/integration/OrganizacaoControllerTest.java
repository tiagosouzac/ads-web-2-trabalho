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

import com.web.eventos.entities.Organizacao;
import com.web.eventos.entities.TipoOrganizacao;
import com.web.eventos.repositories.OrganizacaoRepository;
import com.web.eventos.security.CustomUserDetails;

public class OrganizacaoControllerTest extends BaseIntegrationTest {

    @Autowired
    private OrganizacaoRepository organizacaoRepository;

    private CustomUserDetails getOrganizacaoUser(Organizacao organizacao) {
        return new CustomUserDetails(
                organizacao.getId(),
                organizacao.getEmail(),
                organizacao.getSenha(),
                organizacao.getNome(),
                null,
                "ORGANIZACAO",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ORGANIZACAO")));
    }

    @Test
    @DisplayName("Deve exibir formulário de cadastro de organização")
    void deveExibirFormularioCadastro() throws Exception {
        mockMvc.perform(get("/organizacoes/cadastrar"))
                .andExpect(status().isOk())
                .andExpect(view().name("organizacoes/cadastrar"))
                .andExpect(model().attributeExists("organizacao"));
    }

    @Test
    @DisplayName("Deve cadastrar organização com sucesso")
    void deveCadastrarOrganizacaoComSucesso() throws Exception {
        mockMvc.perform(multipart("/organizacoes/cadastrar")
                .param("nome", "Nova Organização")
                .param("email", "nova.org@email.com")
                .param("senha", "senha123")
                .param("cnpj", "12.345.678/0001-90")
                .param("tipo", "PJ")
                .param("telefone", "(11) 98765-4321")
                .param("endereco", "Rua Teste, 123")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("entrar"))
                .andExpect(model().attributeExists("mensagem"));
    }

    @Test
    @DisplayName("Deve falhar ao cadastrar organização com CNPJ duplicado")
    void deveFalharAoCadastrarOrganizacaoComCnpjDuplicado() throws Exception {
        // Primeiro cadastro
        mockMvc.perform(multipart("/organizacoes/cadastrar")
                .param("nome", "Org 1")
                .param("email", "org1@email.com")
                .param("senha", "senha123")
                .param("cnpj", "99.999.999/0001-99")
                .param("tipo", "PJ")
                .param("telefone", "(11) 98765-4321")
                .param("endereco", "Rua Teste, 123")
                .with(csrf()))
                .andExpect(status().isOk());

        // Segundo cadastro com mesmo CNPJ
        mockMvc.perform(multipart("/organizacoes/cadastrar")
                .param("nome", "Org 2")
                .param("email", "org2@email.com")
                .param("senha", "senha123")
                .param("cnpj", "99.999.999/0001-99")
                .param("tipo", "PJ")
                .param("telefone", "(11) 98765-4321")
                .param("endereco", "Rua Teste, 123")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("organizacoes/cadastrar"))
                .andExpect(model().attributeHasFieldErrors("organizacao", "cnpj"));
    }

    @Test
    @DisplayName("Deve exibir formulário de edição de perfil")
    void deveExibirFormularioEdicao() throws Exception {
        Organizacao organizacao = Organizacao.builder()
                .nome("Org Edição")
                .email("org.edicao@teste.com")
                .senha("senha123")
                .cnpj("11.111.111/0001-11")
                .tipo(TipoOrganizacao.PJ)
                .telefone("(11) 99999-9999")
                .endereco("Rua Teste")
                .build();
        organizacaoRepository.save(organizacao);

        mockMvc.perform(get("/organizacoes/perfil/editar")
                .with(user(getOrganizacaoUser(organizacao))))
                .andExpect(status().isOk())
                .andExpect(view().name("organizacoes/editar"))
                .andExpect(model().attributeExists("organizacao"));
    }

    @Test
    @DisplayName("Deve editar perfil com sucesso")
    void deveEditarPerfil() throws Exception {
        Organizacao organizacao = Organizacao.builder()
                .nome("Org Para Editar")
                .email("org.editar@teste.com")
                .senha("senha123")
                .cnpj("22.222.222/0001-22")
                .tipo(TipoOrganizacao.PJ)
                .telefone("(11) 99999-9999")
                .endereco("Rua Teste")
                .build();
        organizacaoRepository.save(organizacao);

        mockMvc.perform(multipart("/organizacoes/perfil/editar")
                .param("nome", "Org Editada")
                .param("email", "org.editada@teste.com")
                .param("senha", "senha123")
                .param("cnpj", "22.222.222/0001-22")
                .param("tipo", "PJ")
                .param("telefone", "(11) 88888-8888")
                .param("endereco", "Rua Editada")
                .with(user(getOrganizacaoUser(organizacao)))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("organizacoes/editar"))
                .andExpect(model().attributeExists("mensagem"));
    }

    @Test
    @DisplayName("Deve excluir conta com sucesso")
    void deveExcluirConta() throws Exception {
        Organizacao organizacao = Organizacao.builder()
                .nome("Org Para Excluir")
                .email("org.excluir@teste.com")
                .senha("senha123")
                .cnpj("33.333.333/0001-33")
                .tipo(TipoOrganizacao.PJ)
                .telefone("(11) 99999-9999")
                .endereco("Rua Teste")
                .build();
        organizacaoRepository.save(organizacao);

        mockMvc.perform(get("/organizacoes/excluir")
                .with(user(getOrganizacaoUser(organizacao))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/logout"));
    }
}
