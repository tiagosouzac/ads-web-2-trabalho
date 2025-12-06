package com.web.eventos.unit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.web.eventos.config.WithMockCustomUser;
import com.web.eventos.controllers.LocalController;
import com.web.eventos.entities.Estado;
import com.web.eventos.entities.Local;
import com.web.eventos.entities.Organizacao;
import com.web.eventos.services.LocalService;
import com.web.eventos.services.OrganizacaoService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(LocalController.class)
@DisplayName("Testes do LocalController")
@WithMockCustomUser
public class LocalControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LocalService localService;

    @MockitoBean
    private OrganizacaoService organizacaoService;

    @Test
    @DisplayName("Deve listar locais")
    void deveListarLocais() throws Exception {
        Organizacao organizacao = new Organizacao();
        organizacao.setId(1);

        when(organizacaoService.findById(1)).thenReturn(organizacao);
        when(localService.findByOrganizacao(organizacao)).thenReturn(List.of());

        mockMvc.perform(get("/locais"))
                .andExpect(status().isOk())
                .andExpect(view().name("locais/listar"))
                .andExpect(model().attributeExists("locais"));
    }

    @Test
    @DisplayName("Deve retornar a view de cadastrar local")
    void deveRetornarViewCadastrarLocal() throws Exception {
        mockMvc.perform(get("/locais/cadastrar"))
                .andExpect(status().isOk())
                .andExpect(view().name("locais/cadastrar"))
                .andExpect(model().attributeExists("local"));
    }

    @Test
    @DisplayName("Deve salvar local com sucesso")
    void deveSalvarLocalComSucesso() throws Exception {
        Organizacao organizacao = new Organizacao();
        organizacao.setId(1);

        when(organizacaoService.findById(1)).thenReturn(organizacao);
        when(localService.salvar(any(Local.class))).thenReturn(new Local());

        mockMvc.perform(post("/locais/cadastrar")
                .param("nome", "Local Teste")
                .param("endereco", "Rua A, 123")
                .param("cidade", "São Paulo")
                .param("estado", "SP")
                .param("cep", "01234-567")
                .param("capacidade", "100")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/locais"))
                .andExpect(flash().attribute("mensagem", "Local cadastrado com sucesso!"));
    }

    @Test
    @DisplayName("Deve retornar a view de editar local")
    void deveRetornarViewEditarLocal() throws Exception {
        Organizacao organizacao = new Organizacao();
        organizacao.setId(1);

        Local local = new Local();
        local.setId(1);
        local.setOrganizacao(organizacao);
        local.setEstado(Estado.SP);

        when(localService.findById(1)).thenReturn(local);

        mockMvc.perform(get("/locais/editar/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("locais/editar"))
                .andExpect(model().attributeExists("local"));
    }

    @Test
    @DisplayName("Deve atualizar local com sucesso")
    void deveAtualizarLocalComSucesso() throws Exception {
        Organizacao organizacao = new Organizacao();
        organizacao.setId(1);

        Local existing = new Local();
        existing.setId(1);
        existing.setOrganizacao(organizacao);
        existing.setEstado(Estado.SP);

        when(localService.findById(1)).thenReturn(existing);
        when(localService.atualizar(any(Local.class))).thenReturn(new Local());

        mockMvc.perform(post("/locais/editar/1")
                .param("nome", "Local Atualizado")
                .param("endereco", "Rua B, 456")
                .param("cidade", "São Paulo")
                .param("estado", "SP")
                .param("cep", "01234-567")
                .param("capacidade", "200")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/locais"))
                .andExpect(flash().attribute("mensagem", "Local atualizado com sucesso!"));
    }

    @Test
    @DisplayName("Deve excluir local")
    void deveExcluirLocal() throws Exception {
        Organizacao organizacao = new Organizacao();
        organizacao.setId(1);

        Local local = new Local();
        local.setId(1);
        local.setOrganizacao(organizacao);

        when(localService.findById(1)).thenReturn(local);
        doNothing().when(localService).excluir(1);

        mockMvc.perform(get("/locais/excluir/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/locais"))
                .andExpect(flash().attribute("mensagem", "Local excluído com sucesso!"));
    }

    @Test
    @DisplayName("Deve tratar erros de validação ao salvar local")
    void deveTratarErrosDeValidacaoAoSalvarLocal() throws Exception {
        mockMvc.perform(post("/locais/cadastrar")
                .param("nome", "") // Inválido
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("locais/cadastrar"));
    }

    @Test
    @DisplayName("Deve tratar exceção genérica ao salvar local")
    void deveTratarExcecaoGenericaAoSalvarLocal() throws Exception {
        Organizacao organizacao = new Organizacao();
        organizacao.setId(1);

        when(organizacaoService.findById(1)).thenReturn(organizacao);
        when(localService.salvar(any(Local.class))).thenThrow(new RuntimeException("Erro genérico"));

        mockMvc.perform(post("/locais/cadastrar")
                .param("nome", "Local Teste")
                .param("endereco", "Rua A, 123")
                .param("cidade", "São Paulo")
                .param("estado", "SP")
                .param("cep", "01234-567")
                .param("capacidade", "100")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("locais/cadastrar"))
                .andExpect(model().attribute("erro", "Ocorreu um erro ao cadastrar o local. Tente novamente."));
    }

    @Test
    @DisplayName("Deve retornar erro ao editar local não encontrado")
    void deveRetornarErroAoEditarLocalNaoEncontrado() throws Exception {
        when(localService.findById(1)).thenReturn(null);

        mockMvc.perform(get("/locais/editar/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("locais/listar"))
                .andExpect(model().attributeExists("erro"));
    }

    @Test
    @DisplayName("Deve retornar erro ao editar local de outra organização")
    void deveRetornarErroAoEditarLocalDeOutraOrganizacao() throws Exception {
        Organizacao organizacao = new Organizacao();
        organizacao.setId(2); // Outra organização

        Local local = new Local();
        local.setId(1);
        local.setOrganizacao(organizacao);

        when(localService.findById(1)).thenReturn(local);

        mockMvc.perform(get("/locais/editar/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("locais/listar"))
                .andExpect(model().attributeExists("erro"));
    }

    @Test
    @DisplayName("Deve tratar erros de validação ao atualizar local")
    void deveTratarErrosDeValidacaoAoAtualizarLocal() throws Exception {
        Organizacao organizacao = new Organizacao();
        organizacao.setId(1);

        Local local = new Local();
        local.setId(1);
        local.setOrganizacao(organizacao);
        local.setEstado(Estado.SP); // Set estado

        when(localService.findById(1)).thenReturn(local);

        mockMvc.perform(post("/locais/editar/1")
                .param("nome", "") // Inválido
                .param("estado", "SP") // Adicionado estado
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("locais/editar"));
    }

    @Test
    @DisplayName("Deve retornar erro ao atualizar local não encontrado")
    void deveRetornarErroAoAtualizarLocalNaoEncontrado() throws Exception {
        when(localService.findById(1)).thenReturn(null);

        mockMvc.perform(post("/locais/editar/1")
                .param("nome", "Local")
                .param("endereco", "Rua B, 456")
                .param("cidade", "São Paulo")
                .param("estado", "SP")
                .param("cep", "01234-567")
                .param("capacidade", "200")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("locais/editar"))
                .andExpect(model().attributeExists("erro"));
    }

    @Test
    @DisplayName("Deve retornar erro ao atualizar local de outra organização")
    void deveRetornarErroAoAtualizarLocalDeOutraOrganizacao() throws Exception {
        Organizacao organizacao = new Organizacao();
        organizacao.setId(2); // Outra organização

        Local local = new Local();
        local.setId(1);
        local.setOrganizacao(organizacao);

        when(localService.findById(1)).thenReturn(local);

        mockMvc.perform(post("/locais/editar/1")
                .param("nome", "Local")
                .param("endereco", "Rua B, 456")
                .param("cidade", "São Paulo")
                .param("estado", "SP")
                .param("cep", "01234-567")
                .param("capacidade", "200")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("locais/editar"))
                .andExpect(model().attributeExists("erro"));
    }

    @Test
    @DisplayName("Deve tratar IllegalArgumentException ao atualizar local")
    void deveTratarIllegalArgumentExceptionAoAtualizarLocal() throws Exception {
        Organizacao organizacao = new Organizacao();
        organizacao.setId(1);

        Local local = new Local();
        local.setId(1);
        local.setOrganizacao(organizacao);
        local.setEstado(Estado.SP); // Set estado

        when(localService.findById(1)).thenReturn(local);
        when(localService.atualizar(any(Local.class))).thenThrow(new IllegalArgumentException("Erro de argumento"));

        mockMvc.perform(post("/locais/editar/1")
                .param("nome", "Local Atualizado")
                .param("endereco", "Rua B, 456")
                .param("cidade", "São Paulo")
                .param("estado", "SP")
                .param("cep", "01234-567")
                .param("capacidade", "200")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("locais/editar"))
                .andExpect(model().attribute("erro", "Erro de argumento"));
    }

    @Test
    @DisplayName("Deve tratar exceção genérica ao atualizar local")
    void deveTratarExcecaoGenericaAoAtualizarLocal() throws Exception {
        Organizacao organizacao = new Organizacao();
        organizacao.setId(1);

        Local local = new Local();
        local.setId(1);
        local.setOrganizacao(organizacao);
        local.setEstado(Estado.SP); // Set estado

        when(localService.findById(1)).thenReturn(local);
        when(localService.atualizar(any(Local.class))).thenThrow(new RuntimeException("Erro genérico"));

        mockMvc.perform(post("/locais/editar/1")
                .param("nome", "Local Atualizado")
                .param("endereco", "Rua B, 456")
                .param("cidade", "São Paulo")
                .param("estado", "SP")
                .param("cep", "01234-567")
                .param("capacidade", "200")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("locais/editar"))
                .andExpect(model().attribute("erro", "Ocorreu um erro ao atualizar o local. Tente novamente."));
    }

    @Test
    @DisplayName("Deve retornar erro ao excluir local não encontrado")
    void deveRetornarErroAoExcluirLocalNaoEncontrado() throws Exception {
        when(localService.findById(1)).thenReturn(null);

        mockMvc.perform(get("/locais/excluir/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/locais"));
    }

    @Test
    @DisplayName("Deve retornar erro ao excluir local de outra organização")
    void deveRetornarErroAoExcluirLocalDeOutraOrganizacao() throws Exception {
        Organizacao organizacao = new Organizacao();
        organizacao.setId(2); // Outra organização

        Local local = new Local();
        local.setId(1);
        local.setOrganizacao(organizacao);

        when(localService.findById(1)).thenReturn(local);

        mockMvc.perform(get("/locais/excluir/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/locais"));
    }
}