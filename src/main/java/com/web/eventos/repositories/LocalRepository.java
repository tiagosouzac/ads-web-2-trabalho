package com.web.eventos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.web.eventos.entities.Local;
import com.web.eventos.entities.Organizacao;

import java.util.List;

public interface LocalRepository extends JpaRepository<Local, Integer> {
    List<Local> findByOrganizacao(Organizacao organizacao);

    @Query("SELECT DISTINCT l.cidade FROM Local l ORDER BY l.cidade")
    List<String> findDistinctCidade();
}
