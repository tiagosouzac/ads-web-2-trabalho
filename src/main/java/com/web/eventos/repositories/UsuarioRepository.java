package com.web.eventos.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.web.eventos.entities.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);
}
