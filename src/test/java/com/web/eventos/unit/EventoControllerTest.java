package com.web.eventos.unit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.web.eventos.config.WithMockCustomUser;
import com.web.eventos.controllers.EventoController;
import com.web.eventos.entities.Categoria;
import com.web.eventos.entities.Evento;
import com.web.eventos.entities.EventoStatus;
import com.web.eventos.entities.Local;
import com.web.eventos.entities.Midia;
import com.web.eventos.entities.Organizacao;
import com.web.eventos.security.CustomUserDetails;
import com.web.eventos.services.AutenticacaoService;
import com.web.eventos.services.ComentarioService;
import com.web.eventos.services.EventoService;
import com.web.eventos.services.InteressadoService;
import com.web.eventos.services.LocalService;
import com.web.eventos.services.MidiaService;
import com.web.eventos.services.OrganizacaoService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(EventoController.class)
@DisplayName("Testes do EventoController")
@WithMockCustomUser
public class EventoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventoService eventoService;

    @MockitoBean
    private OrganizacaoService organizacaoService;

    @MockitoBean
    private LocalService localService;

    @MockitoBean
    private MidiaService midiaService;

    @MockitoBean
    private InteressadoService interessadoService;

    @MockitoBean
    private AutenticacaoService autenticacaoService;

    @MockitoBean
    private ComentarioService comentarioService;

    @Test
    @DisplayName("Deve listar eventos da organização")
    void deveListarEventos() throws Exception {
        Organizacao organizacao = new Organizacao();
        organizacao.setId(1);

        Evento evento = new Evento();
        evento.setId(1);
        evento.setStatus(EventoStatus.PUBLICADO);

        when(organizacaoService.findById(1)).thenReturn(organizacao);
        when(eventoService.findByOrganizacao(organizacao)).thenReturn(List.of(evento));
        when(interessadoService.countInteressadosByEventoId(1)).thenReturn(10L);

        mockMvc.perform(get("/eventos"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("eventos"))
                .andExpect(model().attributeExists("interessadosCountMap"));
    }

    @Test
    @DisplayName("Deve buscar eventos")
    void deveBuscarEventos() throws Exception {
        Evento evento = new Evento();
        evento.setId(1);
        evento.setCategoria(Categoria.CONFERENCIAS);
        Local local = new Local();
        local.setNome("Local Teste");
        evento.setLocal(local);

        Page<Evento> page = new PageImpl<>(List.of(evento), PageRequest.of(0, 12), 1);

        when(eventoService.buscar(any(), any(), any(), any(), any())).thenReturn(page);
        when(interessadoService.countInteressadosByEventoId(1)).thenReturn(5L);

        mockMvc.perform(get("/eventos/buscar"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("eventosPage"))
                .andExpect(model().attributeExists("interessadosCountMap"));
    }

    @Test
    @DisplayName("Deve mostrar detalhes do evento")
    void deveMostrarDetalhesEvento() throws Exception {
        Evento evento = new Evento();
        evento.setId(1);
        evento.setCategoria(Categoria.CONFERENCIAS);

        Local local = new Local();
        local.setNome("Local Teste");
        evento.setLocal(local);

        CustomUserDetails user = new CustomUserDetails(1, "test@example.com", "password", "Test User", null, "USUARIO",
                Set.of(new SimpleGrantedAuthority("ROLE_USUARIO")));

        when(eventoService.findById(1)).thenReturn(evento);
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(user);
        when(interessadoService.countInteressadosByEventoId(1)).thenReturn(20L);
        when(interessadoService.isInteressado(1, 1)).thenReturn(true);
        when(comentarioService.getComentariosPriorizados(1, 1, 3)).thenReturn(List.of());
        when(comentarioService.getTotalComentarios(1)).thenReturn(0L);
        when(eventoService.findByCategoriaExcluding(Categoria.CONFERENCIAS, 1, 4)).thenReturn(List.of());
        when(interessadoService.countInteressadosByEventoId(anyInt())).thenReturn(0L);

        mockMvc.perform(get("/eventos/1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("evento"))
                .andExpect(model().attributeExists("interessadosCount"))
                .andExpect(model().attribute("isInteressado", true));
    }

    @Test
    @DisplayName("Deve retornar erro se evento não encontrado")
    void deveRetornarErroEventoNaoEncontrado() throws Exception {
        when(eventoService.findById(1)).thenReturn(null);

        mockMvc.perform(get("/eventos/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/erros/404"));
    }

    @Test
    @DisplayName("Deve retornar a view de cadastrar evento")
    void deveRetornarViewCadastrarEvento() throws Exception {
        Organizacao organizacao = new Organizacao();
        organizacao.setId(1);

        when(organizacaoService.findById(1)).thenReturn(organizacao);
        when(localService.findByOrganizacao(organizacao)).thenReturn(List.of());

        mockMvc.perform(get("/eventos/cadastrar"))
                .andExpect(status().isOk())
                .andExpect(view().name("eventos/cadastrar"))
                .andExpect(model().attributeExists("evento"))
                .andExpect(model().attributeExists("locais"))
                .andExpect(model().attributeExists("categorias"));
    }

    @Test
    @DisplayName("Deve salvar evento com sucesso")
    void deveSalvarEventoComSucesso() throws Exception {
        Organizacao organizacao = new Organizacao();
        organizacao.setId(1);

        Evento eventoSalvo = new Evento();
        eventoSalvo.setId(1);

        MockMultipartFile midia = new MockMultipartFile("midias", "imagem.jpg", "image/jpeg", "fake image".getBytes());

        when(organizacaoService.findById(1)).thenReturn(organizacao);
        when(eventoService.salvar(any(Evento.class))).thenReturn(eventoSalvo);
        when(midiaService.uploadArquivo(any(), any(Evento.class))).thenReturn(new Midia());
        when(localService.findByOrganizacao(organizacao)).thenReturn(List.of(new Local()));

        mockMvc.perform(multipart("/eventos/cadastrar")
                .file(midia)
                .param("nome", "Evento Teste")
                .param("descricao", "Descrição")
                .param("categoria", "CONFERENCIAS")
                .param("dataInicio", "2025-12-10T10:00")
                .param("dataFim", "2025-12-10T12:00")
                .param("preco", "100.00")
                .param("local.id", "1")
                .param("status", "PUBLICADO")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/eventos"))
                .andExpect(flash().attribute("mensagem", "Evento cadastrado com sucesso!"));
    }

    @Test
    @DisplayName("Deve retornar a view de editar evento")
    void deveRetornarViewEditarEvento() throws Exception {
        Organizacao organizacao = new Organizacao();
        organizacao.setId(1);

        Evento evento = new Evento();
        evento.setId(1);
        evento.setOrganizacao(organizacao);
        evento.setCategoria(Categoria.CONFERENCIAS);

        when(eventoService.findById(1)).thenReturn(evento);
        when(localService.findByOrganizacao(organizacao)).thenReturn(List.of());

        mockMvc.perform(get("/eventos/editar/1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("evento"));
    }

    @Test
    @DisplayName("Deve atualizar evento com sucesso")
    void deveAtualizarEventoComSucesso() throws Exception {
        Organizacao organizacao = new Organizacao();
        organizacao.setId(1);

        Evento existing = new Evento();
        existing.setId(1);
        existing.setOrganizacao(organizacao);
        existing.setCategoria(Categoria.CONFERENCIAS);

        Evento eventoAtualizado = new Evento();
        eventoAtualizado.setId(1);

        when(eventoService.findById(1)).thenReturn(existing);
        when(eventoService.atualizar(any(Evento.class))).thenReturn(eventoAtualizado);
        doNothing().when(midiaService).removerMidia(anyInt());
        when(midiaService.uploadArquivo(any(), any(Evento.class))).thenReturn(new Midia());
        when(localService.findByOrganizacao(organizacao)).thenReturn(List.of(new Local()));

        mockMvc.perform(multipart("/eventos/editar/1")
                .param("nome", "Evento Atualizado")
                .param("descricao", "Descrição Atualizada")
                .param("categoria", "CONFERENCIAS")
                .param("dataInicio", "2025-12-11T11:00")
                .param("dataFim", "2025-12-11T13:00")
                .param("preco", "150.00")
                .param("local.id", "1")
                .param("status", "PUBLICADO")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/eventos"))
                .andExpect(flash().attribute("mensagem", "Evento atualizado com sucesso!"));
    }

    @Test
    @DisplayName("Deve excluir evento")
    void deveExcluirEvento() throws Exception {
        Organizacao organizacao = new Organizacao();
        organizacao.setId(1);

        Evento evento = new Evento();
        evento.setId(1);
        evento.setOrganizacao(organizacao);

        when(eventoService.findById(1)).thenReturn(evento);
        doNothing().when(eventoService).excluir(1);

        mockMvc.perform(get("/eventos/excluir/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/eventos"))
                .andExpect(flash().attribute("mensagem", "Evento excluído com sucesso!"));
    }

    @Test
    @DisplayName("Deve mostrar detalhes do evento sem usuário logado")
    void deveMostrarDetalhesEventoSemUsuarioLogado() throws Exception {
        Evento evento = new Evento();
        evento.setId(1);
        evento.setCategoria(Categoria.CONFERENCIAS);

        Local local = new Local();
        local.setNome("Local Teste");
        evento.setLocal(local);

        when(eventoService.findById(1)).thenReturn(evento);
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(null);
        when(interessadoService.countInteressadosByEventoId(1)).thenReturn(20L);
        when(comentarioService.getComentariosPriorizados(1, null, 3)).thenReturn(List.of());
        when(comentarioService.getTotalComentarios(1)).thenReturn(0L);
        when(eventoService.findByCategoriaExcluding(Categoria.CONFERENCIAS, 1, 4)).thenReturn(List.of());

        mockMvc.perform(get("/eventos/1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("evento"))
                .andExpect(model().attribute("isInteressado", false));
    }

    @Test
    @DisplayName("Deve tratar erros de validação ao salvar evento")
    void deveTratarErrosDeValidacaoAoSalvarEvento() throws Exception {
        Organizacao organizacao = new Organizacao();
        organizacao.setId(1);

        when(organizacaoService.findById(1)).thenReturn(organizacao);
        when(localService.findByOrganizacao(organizacao)).thenReturn(List.of());

        mockMvc.perform(multipart("/eventos/cadastrar")
                .param("nome", "") // Inválido
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("eventos/cadastrar"));
    }

    @Test
    @DisplayName("Deve tratar exceção genérica ao salvar evento")
    void deveTratarExcecaoGenericaAoSalvarEvento() throws Exception {
        Organizacao organizacao = new Organizacao();
        organizacao.setId(1);

        when(organizacaoService.findById(1)).thenReturn(organizacao);
        when(eventoService.salvar(any(Evento.class))).thenThrow(new RuntimeException("Erro genérico"));
        when(localService.findByOrganizacao(organizacao)).thenReturn(List.of());

        mockMvc.perform(multipart("/eventos/cadastrar")
                .param("nome", "Evento Teste")
                .param("descricao", "Descrição")
                .param("categoria", "CONFERENCIAS")
                .param("dataInicio", "2025-12-10T10:00")
                .param("dataFim", "2025-12-10T12:00")
                .param("preco", "100.00")
                .param("local.id", "1")
                .param("status", "PUBLICADO")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("eventos/cadastrar"))
                .andExpect(model().attribute("erro", "Ocorreu um erro ao cadastrar o evento. Tente novamente."));
    }

    @Test
    @DisplayName("Deve retornar erro ao editar evento não encontrado")
    void deveRetornarErroAoEditarEventoNaoEncontrado() throws Exception {
        when(eventoService.findById(1)).thenReturn(null);

        mockMvc.perform(get("/eventos/editar/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("eventos/listar"))
                .andExpect(model().attributeExists("erro"));
    }

    @Test
    @DisplayName("Deve retornar erro ao editar evento de outra organização")
    void deveRetornarErroAoEditarEventoDeOutraOrganizacao() throws Exception {
        Organizacao organizacao = new Organizacao();
        organizacao.setId(2); // Outra organização

        Evento evento = new Evento();
        evento.setId(1);
        evento.setOrganizacao(organizacao);

        when(eventoService.findById(1)).thenReturn(evento);

        mockMvc.perform(get("/eventos/editar/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("eventos/listar"))
                .andExpect(model().attributeExists("erro"));
    }

    @Test
    @DisplayName("Deve tratar erros de validação ao atualizar evento")
    void deveTratarErrosDeValidacaoAoAtualizarEvento() throws Exception {
        Organizacao organizacao = new Organizacao();
        organizacao.setId(1);

        Evento evento = new Evento();
        evento.setId(1);
        evento.setOrganizacao(organizacao);
        evento.setCategoria(Categoria.CONFERENCIAS);

        when(eventoService.findById(1)).thenReturn(evento);
        when(localService.findByOrganizacao(organizacao)).thenReturn(List.of());

        mockMvc.perform(multipart("/eventos/editar/1")
                .param("nome", "") // Inválido
                .param("categoria", "CONFERENCIAS") // Necessário para evitar erro no template
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("eventos/editar"));
    }

    @Test
    @DisplayName("Deve retornar erro ao atualizar evento não encontrado")
    void deveRetornarErroAoAtualizarEventoNaoEncontrado() throws Exception {
        when(eventoService.findById(1)).thenReturn(null);

        mockMvc.perform(multipart("/eventos/editar/1")
                .param("nome", "Evento")
                .param("descricao", "Descrição")
                .param("categoria", "CONFERENCIAS")
                .param("dataInicio", "2025-12-10T10:00")
                .param("dataFim", "2025-12-10T12:00")
                .param("preco", "100.00")
                .param("local.id", "1")
                .param("status", "PUBLICADO")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("eventos/editar"))
                .andExpect(model().attributeExists("erro"));
    }

    @Test
    @DisplayName("Deve retornar erro ao atualizar evento de outra organização")
    void deveRetornarErroAoAtualizarEventoDeOutraOrganizacao() throws Exception {
        Organizacao organizacao = new Organizacao();
        organizacao.setId(2); // Outra organização

        Evento evento = new Evento();
        evento.setId(1);
        evento.setOrganizacao(organizacao);

        when(eventoService.findById(1)).thenReturn(evento);

        mockMvc.perform(multipart("/eventos/editar/1")
                .param("nome", "Evento")
                .param("descricao", "Descrição")
                .param("categoria", "CONFERENCIAS")
                .param("dataInicio", "2025-12-10T10:00")
                .param("dataFim", "2025-12-10T12:00")
                .param("preco", "100.00")
                .param("local.id", "1")
                .param("status", "PUBLICADO")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("eventos/editar"))
                .andExpect(model().attributeExists("erro"));
    }

    @Test
    @DisplayName("Deve tratar IllegalArgumentException ao atualizar evento")
    void deveTratarIllegalArgumentExceptionAoAtualizarEvento() throws Exception {
        Organizacao organizacao = new Organizacao();
        organizacao.setId(1);

        Evento evento = new Evento();
        evento.setId(1);
        evento.setOrganizacao(organizacao);
        evento.setCategoria(Categoria.CONFERENCIAS);

        when(eventoService.findById(1)).thenReturn(evento);
        when(eventoService.atualizar(any(Evento.class))).thenThrow(new IllegalArgumentException("Erro de argumento"));
        when(localService.findByOrganizacao(organizacao)).thenReturn(List.of());

        mockMvc.perform(multipart("/eventos/editar/1")
                .param("nome", "Evento")
                .param("descricao", "Descrição")
                .param("categoria", "CONFERENCIAS")
                .param("dataInicio", "2025-12-10T10:00")
                .param("dataFim", "2025-12-10T12:00")
                .param("preco", "100.00")
                .param("local.id", "1")
                .param("status", "PUBLICADO")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("eventos/editar"))
                .andExpect(model().attribute("erro", "Erro de argumento"));
    }

    @Test
    @DisplayName("Deve tratar exceção genérica ao atualizar evento")
    void deveTratarExcecaoGenericaAoAtualizarEvento() throws Exception {
        Organizacao organizacao = new Organizacao();
        organizacao.setId(1);

        Evento evento = new Evento();
        evento.setId(1);
        evento.setOrganizacao(organizacao);
        evento.setCategoria(Categoria.CONFERENCIAS);

        when(eventoService.findById(1)).thenReturn(evento);
        when(eventoService.atualizar(any(Evento.class))).thenThrow(new RuntimeException("Erro genérico"));
        when(localService.findByOrganizacao(organizacao)).thenReturn(List.of());

        mockMvc.perform(multipart("/eventos/editar/1")
                .param("nome", "Evento")
                .param("descricao", "Descrição")
                .param("categoria", "CONFERENCIAS")
                .param("dataInicio", "2025-12-10T10:00")
                .param("dataFim", "2025-12-10T12:00")
                .param("preco", "100.00")
                .param("local.id", "1")
                .param("status", "PUBLICADO")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("eventos/editar"))
                .andExpect(model().attribute("erro", "Ocorreu um erro ao atualizar o evento. Tente novamente."));
    }

    @Test
    @DisplayName("Deve retornar erro ao excluir evento não encontrado")
    void deveRetornarErroAoExcluirEventoNaoEncontrado() throws Exception {
        when(eventoService.findById(1)).thenReturn(null);

        mockMvc.perform(get("/eventos/excluir/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/eventos"));
    }

    @Test
    @DisplayName("Deve retornar erro ao excluir evento de outra organização")
    void deveRetornarErroAoExcluirEventoDeOutraOrganizacao() throws Exception {
        Organizacao organizacao = new Organizacao();
        organizacao.setId(2); // Outra organização

        Evento evento = new Evento();
        evento.setId(1);
        evento.setOrganizacao(organizacao);

        when(eventoService.findById(1)).thenReturn(evento);

        mockMvc.perform(get("/eventos/excluir/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/eventos"));
    }
}