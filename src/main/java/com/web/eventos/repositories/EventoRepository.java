package com.web.eventos.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.web.eventos.entities.Categoria;
import com.web.eventos.entities.Evento;
import com.web.eventos.entities.EventoStatus;
import com.web.eventos.entities.Organizacao;

import java.time.LocalDateTime;
import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Integer> {
        List<Evento> findByStatus(EventoStatus status);

        List<Evento> findByCategoria(Categoria categoria);

        List<Evento> findFirst4ByCategoriaAndStatus(Categoria categoria, EventoStatus status);

        List<Evento> findByDataInicioBetween(LocalDateTime inicio, LocalDateTime fim);

        List<Evento> findByOrganizacao(Organizacao organizacao);

        List<Evento> findByCategoriaAndStatusAndIdNot(Categoria categoria, EventoStatus status, Integer id);

        @Query("SELECT DISTINCT e FROM Evento e LEFT JOIN FETCH e.midias WHERE e.status = 'PUBLICADO' " +
                        "AND (COALESCE(:query, '') = '' OR LOWER(e.nome) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(COALESCE(e.descricao, '')) LIKE LOWER(CONCAT('%', :query, '%'))) "
                        +
                        "AND (:categoria IS NULL OR e.categoria = :categoria) " +
                        "AND (:cidade IS NULL OR e.local.cidade = :cidade) " +
                        "AND (e.dataInicio BETWEEN :dataInicioDia AND :dataFimDia)")
        Page<Evento> buscarComFiltros(
                        @Param("query") String query,
                        @Param("categoria") Categoria categoria,
                        @Param("cidade") String cidade,
                        @Param("dataInicioDia") LocalDateTime dataInicioDia,
                        @Param("dataFimDia") LocalDateTime dataFimDia,
                        Pageable pageable);
}
