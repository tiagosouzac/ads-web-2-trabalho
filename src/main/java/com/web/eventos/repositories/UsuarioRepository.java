package com.web.eventos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.web.eventos.entities.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Usuario findByEmail(String email);
}
