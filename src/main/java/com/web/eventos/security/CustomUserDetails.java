package com.web.eventos.security;

import com.web.eventos.entities.Midia;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomUserDetails extends User {
    private final Integer id;
    private final String nome;
    private final String email;
    private final Midia avatar;
    private final String tipo; // "USUARIO" ou "ORGANIZACAO"

    public CustomUserDetails(Integer id, String email, String senha, String nome, Midia avatar, String tipo,
            Collection<? extends GrantedAuthority> authorities) {
        super(email, senha, authorities);
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.avatar = avatar;
        this.tipo = tipo;
    }
}
