package com.web.eventos.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.web.eventos.entities.Categoria;
import com.web.eventos.entities.Evento;
import com.web.eventos.entities.EventoStatus;
import com.web.eventos.entities.Interessado;
import com.web.eventos.entities.Organizacao;
import com.web.eventos.entities.TipoOrganizacao;
import com.web.eventos.entities.Usuario;
import com.web.eventos.entities.UsuarioRole;
import com.web.eventos.repositories.EventoRepository;
import com.web.eventos.repositories.InteressadoRepository;
import com.web.eventos.repositories.OrganizacaoRepository;
import com.web.eventos.repositories.UsuarioRepository;
import com.web.eventos.security.CustomUserDetails;

public class InteressadoControllerTest extends BaseIntegrationTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private OrganizacaoRepository organizacaoRepository;

    @Autowired
    private InteressadoRepository interessadoRepository;

    private Usuario usuario;
    private Evento evento;
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .nome("Usuário Teste")
                .email("usuario@teste.com")
                .senha("senha123")
                .cpf("123.456.789-00")
                .role(UsuarioRole.USER)
                .build();
        usuarioRepository.save(usuario);

        Organizacao organizacao = Organizacao.builder()
                .nome("Org Teste")
                .email("org@teste.com")
                .senha("senha123")
                .cnpj("12.345.678/0001-90")
                .tipo(TipoOrganizacao.PJ)
                .telefone("(11) 99999-9999")
                .endereco("Rua Teste")
                .build();
        organizacaoRepository.save(organizacao);

        evento = Evento.builder()
                .nome("Evento Teste")
                .descricao("Descrição")
                .dataInicio(LocalDateTime.now().plusDays(1))
                .dataFim(LocalDateTime.now().plusDays(2))
                .organizacao(organizacao)
                .categoria(Categoria.MUSICA)
                .status(EventoStatus.PUBLICADO)
                .preco(new BigDecimal("10.0"))
                .build();
        eventoRepository.save(evento);

        userDetails = new CustomUserDetails(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getSenha(),
                usuario.getNome(),
                null,
                "USUARIO",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    @DisplayName("Deve registrar interesse em evento")
    void deveRegistrarInteresse() throws Exception {
        mockMvc.perform(post("/interessados/salvar")
                .with(user(userDetails))
                .with(csrf())
                .param("eventoId", evento.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/eventos/" + evento.getId()))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    @DisplayName("Deve excluir interesse em evento")
    void deveExcluirInteresse() throws Exception {
        Interessado interessado = new Interessado();
        interessado.setUsuario(usuario);
        interessado.setEvento(evento);
        interessadoRepository.save(interessado);

        mockMvc.perform(post("/interessados/excluir")
                .with(user(userDetails))
                .with(csrf())
                .param("eventoId", evento.getId().toString())
                .param("_method", "DELETE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/eventos/" + evento.getId()))
                .andExpect(flash().attributeExists("success"));
    }
}
