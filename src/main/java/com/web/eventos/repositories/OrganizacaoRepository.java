package com.web.eventos.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.web.eventos.entities.Organizacao;

public interface OrganizacaoRepository extends JpaRepository<Organizacao, Integer> {
    Optional<Organizacao> findByEmail(String email);

    Optional<Organizacao> findByCnpj(String cnpj);

    boolean existsByEmail(String email);

    boolean existsByCnpj(String cnpj);
}
