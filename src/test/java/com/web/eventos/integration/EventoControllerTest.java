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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.web.eventos.entities.Categoria;
import com.web.eventos.entities.Estado;
import com.web.eventos.entities.Evento;
import com.web.eventos.entities.EventoStatus;
import com.web.eventos.entities.Local;
import com.web.eventos.entities.Organizacao;
import com.web.eventos.entities.TipoOrganizacao;
import com.web.eventos.repositories.EventoRepository;
import com.web.eventos.repositories.LocalRepository;
import com.web.eventos.repositories.OrganizacaoRepository;
import com.web.eventos.security.CustomUserDetails;

public class EventoControllerTest extends BaseIntegrationTest {

    @Autowired
    private OrganizacaoRepository organizacaoRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private LocalRepository localRepository;

    private Organizacao organizacao;
    private Local local;
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

        local = Local.builder()
                .nome("Local Teste")
                .organizacao(organizacao)
                .cep("12345-678")
                .endereco("Rua do Local")
                .cidade("Cidade Teste")
                .estado(Estado.SP)
                .capacidade(100)
                .build();
        localRepository.save(local);

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
    @DisplayName("Deve listar eventos da organização logada")
    void deveListarEventosDaOrganizacao() throws Exception {
        Evento evento = Evento.builder()
                .nome("Evento Teste")
                .descricao("Descrição")
                .dataInicio(LocalDateTime.now().plusDays(1))
                .dataFim(LocalDateTime.now().plusDays(2))
                .organizacao(organizacao)
                .local(local)
                .categoria(Categoria.MUSICA)
                .status(EventoStatus.PUBLICADO)
                .preco(new BigDecimal("10.0"))
                .build();
        eventoRepository.save(evento);

        mockMvc.perform(get("/eventos")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("eventos/listar"))
                .andExpect(model().attributeExists("eventos"));
    }

    @Test
    @DisplayName("Deve buscar eventos publicamente")
    void deveBuscarEventos() throws Exception {
        mockMvc.perform(get("/eventos/buscar"))
                .andExpect(status().isOk())
                .andExpect(view().name("eventos/busca"))
                .andExpect(model().attributeExists("eventosPage"));
    }

    @Test
    @DisplayName("Deve exibir detalhes do evento")
    void deveExibirDetalhesEvento() throws Exception {
        Evento evento = Evento.builder()
                .nome("Evento Detalhes")
                .descricao("Descrição")
                .dataInicio(LocalDateTime.now().plusDays(1))
                .dataFim(LocalDateTime.now().plusDays(2))
                .organizacao(organizacao)
                .local(local)
                .categoria(Categoria.TEATRO)
                .status(EventoStatus.PUBLICADO)
                .preco(new BigDecimal("20.0"))
                .build();
        eventoRepository.save(evento);

        mockMvc.perform(get("/eventos/" + evento.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("eventos/detalhes"))
                .andExpect(model().attributeExists("evento"));
    }

    @Test
    @DisplayName("Deve exibir formulário de cadastro de evento")
    void deveExibirFormularioCadastro() throws Exception {
        mockMvc.perform(get("/eventos/cadastrar")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("eventos/cadastrar"));
    }

    @Test
    @DisplayName("Deve cadastrar evento com sucesso")
    void deveCadastrarEvento() throws Exception {
        mockMvc.perform(post("/eventos/cadastrar")
                .with(user(userDetails))
                .with(csrf())
                .param("nome", "Novo Evento")
                .param("descricao", "Descrição do novo evento")
                .param("dataInicio", LocalDateTime.now().plusDays(10).toString())
                .param("dataFim", LocalDateTime.now().plusDays(11).toString())
                .param("categoria", "MUSICA")
                .param("status", "RASCUNHO")
                .param("preco", "50.00")
                .param("idadeMinima", "18")
                .param("local.id", local.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/eventos"))
                .andExpect(flash().attributeExists("mensagem"));
    }

    @Test
    @DisplayName("Deve exibir formulário de edição de evento")
    void deveExibirFormularioEdicao() throws Exception {
        Evento evento = Evento.builder()
                .nome("Evento Edição")
                .descricao("Descrição")
                .dataInicio(LocalDateTime.now().plusDays(1))
                .dataFim(LocalDateTime.now().plusDays(2))
                .organizacao(organizacao)
                .local(local)
                .categoria(Categoria.ESPORTES)
                .status(EventoStatus.RASCUNHO)
                .preco(new BigDecimal("30.0"))
                .build();
        eventoRepository.save(evento);

        mockMvc.perform(get("/eventos/editar/" + evento.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("eventos/editar"))
                .andExpect(model().attributeExists("evento"));
    }

    @Test
    @DisplayName("Deve editar evento com sucesso")
    void deveEditarEvento() throws Exception {
        Evento evento = Evento.builder()
                .nome("Evento Para Editar")
                .descricao("Descrição")
                .dataInicio(LocalDateTime.now().plusDays(1))
                .dataFim(LocalDateTime.now().plusDays(2))
                .organizacao(organizacao)
                .local(local)
                .categoria(Categoria.ESPORTES)
                .status(EventoStatus.RASCUNHO)
                .preco(new BigDecimal("30.0"))
                .build();
        eventoRepository.save(evento);

        mockMvc.perform(post("/eventos/editar/" + evento.getId())
                .with(user(userDetails))
                .with(csrf())
                .param("nome", "Evento Editado")
                .param("descricao", "Descrição Editada")
                .param("dataInicio", LocalDateTime.now().plusDays(10).toString())
                .param("dataFim", LocalDateTime.now().plusDays(11).toString())
                .param("categoria", "MUSICA")
                .param("status", "PUBLICADO")
                .param("preco", "60.00")
                .param("idadeMinima", "16")
                .param("local.id", local.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/eventos"))
                .andExpect(flash().attributeExists("mensagem"));
    }

    @Test
    @DisplayName("Deve excluir evento com sucesso")
    void deveExcluirEvento() throws Exception {
        Evento evento = Evento.builder()
                .nome("Evento Para Excluir")
                .descricao("Descrição")
                .dataInicio(LocalDateTime.now().plusDays(1))
                .dataFim(LocalDateTime.now().plusDays(2))
                .organizacao(organizacao)
                .local(local)
                .categoria(Categoria.ESPORTES)
                .status(EventoStatus.RASCUNHO)
                .preco(new BigDecimal("30.0"))
                .build();
        eventoRepository.save(evento);

        mockMvc.perform(get("/eventos/excluir/" + evento.getId())
                .with(user(userDetails)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/eventos"))
                .andExpect(flash().attributeExists("mensagem"));
    }
}
