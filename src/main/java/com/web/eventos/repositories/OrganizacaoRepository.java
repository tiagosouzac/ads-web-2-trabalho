package com.web.eventos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.web.eventos.entities.Organizacao;

public interface OrganizacaoRepository extends JpaRepository<Organizacao, Integer> {
    Organizacao findByCnpj(String cnpj);
}
