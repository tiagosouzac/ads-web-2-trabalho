package com.web.eventos.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.web.eventos.entities.Estado;
import com.web.eventos.entities.Local;
import com.web.eventos.entities.Organizacao;
import com.web.eventos.entities.TipoOrganizacao;
import com.web.eventos.repositories.LocalRepository;
import com.web.eventos.repositories.OrganizacaoRepository;
import com.web.eventos.security.CustomUserDetails;

public class LocalControllerTest extends BaseIntegrationTest {

    @Autowired
    private OrganizacaoRepository organizacaoRepository;

    @Autowired
    private LocalRepository localRepository;

    private Organizacao organizacao;
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        organizacao = Organizacao.builder()
                .nome("Org Teste")
                .email("org@teste.com")
                .senha("senha123")
                .cnpj("12.345.678/0001-90")
                .tipo(TipoOrganizacao.PJ)
                .telefone("(11) 99999-9999")
                .endereco("Rua Teste")
                .build();
        organizacaoRepository.save(organizacao);

        userDetails = new CustomUserDetails(
                organizacao.getId(),
                organizacao.getEmail(),
                organizacao.getSenha(),
                organizacao.getNome(),
                null,
                "ORGANIZACAO",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ORGANIZACAO")));
    }

    @Test
    @DisplayName("Deve listar locais da organização")
    void deveListarLocais() throws Exception {
        Local local = Local.builder()
                .nome("Local Teste")
                .endereco("Endereço Teste")
                .cidade("Cidade Teste")
                .estado(Estado.SP)
                .cep("12345-678")
                .capacidade(100)
                .organizacao(organizacao)
                .build();
        localRepository.save(local);

        mockMvc.perform(get("/locais")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("locais/listar"))
                .andExpect(model().attributeExists("locais"));
    }

    @Test
    @DisplayName("Deve exibir formulário de cadastro de local")
    void deveExibirFormularioCadastro() throws Exception {
        mockMvc.perform(get("/locais/cadastrar")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("locais/cadastrar"));
    }

    @Test
    @DisplayName("Deve cadastrar local com sucesso")
    void deveCadastrarLocal() throws Exception {
        mockMvc.perform(post("/locais/cadastrar")
                .with(user(userDetails))
                .with(csrf())
                .param("nome", "Novo Local")
                .param("endereco", "Rua Nova")
                .param("cidade", "São Paulo")
                .param("estado", "SP")
                .param("cep", "01001-000")
                .param("capacidade", "500"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/locais"))
                .andExpect(flash().attributeExists("mensagem"));
    }

    @Test
    @DisplayName("Deve exibir formulário de edição de local")
    void deveExibirFormularioEdicao() throws Exception {
        Local local = Local.builder()
                .nome("Local Edição")
                .endereco("Endereço Teste")
                .cidade("Cidade Teste")
                .estado(Estado.SP)
                .cep("12345-678")
                .capacidade(100)
                .organizacao(organizacao)
                .build();
        localRepository.save(local);

        mockMvc.perform(get("/locais/editar/" + local.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("locais/editar"))
                .andExpect(model().attributeExists("local"));
    }

    @Test
    @DisplayName("Deve editar local com sucesso")
    void deveEditarLocal() throws Exception {
        Local local = Local.builder()
                .nome("Local Para Editar")
                .endereco("Endereço Teste")
                .cidade("Cidade Teste")
                .estado(Estado.SP)
                .cep("12345-678")
                .capacidade(100)
                .organizacao(organizacao)
                .build();
        localRepository.save(local);

        mockMvc.perform(post("/locais/editar/" + local.getId())
                .with(user(userDetails))
                .with(csrf())
                .param("nome", "Local Editado")
                .param("endereco", "Rua Editada")
                .param("cidade", "Rio de Janeiro")
                .param("estado", "RJ")
                .param("cep", "20000-000")
                .param("capacidade", "200"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/locais"))
                .andExpect(flash().attributeExists("mensagem"));
    }

    @Test
    @DisplayName("Deve excluir local com sucesso")
    void deveExcluirLocal() throws Exception {
        Local local = Local.builder()
                .nome("Local Para Excluir")
                .endereco("Endereço Teste")
                .cidade("Cidade Teste")
                .estado(Estado.SP)
                .cep("12345-678")
                .capacidade(100)
                .organizacao(organizacao)
                .build();
        localRepository.save(local);

        mockMvc.perform(get("/locais/excluir/" + local.getId())
                .with(user(userDetails)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/locais"))
                .andExpect(flash().attributeExists("mensagem"));
    }
}
