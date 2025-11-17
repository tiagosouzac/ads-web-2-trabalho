package com.web.eventos.services;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.web.eventos.entities.Organizacao;
import com.web.eventos.repositories.OrganizacaoRepository;

@Service
public class OrganizacaoService {
    private final OrganizacaoRepository organizacaoRepository;
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    public OrganizacaoService(OrganizacaoRepository organizacaoRepository, @Lazy UsuarioService usuarioService,
            PasswordEncoder passwordEncoder) {
        this.organizacaoRepository = organizacaoRepository;
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
    }

    public Organizacao findByEmail(String email) {
        return organizacaoRepository.findByEmail(email).orElse(null);
    }

    public boolean existsByEmail(String email) {
        return organizacaoRepository.existsByEmail(email);
    }

    public Organizacao salvar(Organizacao organizacao) {
        if (organizacaoRepository.existsByCnpj(organizacao.getCnpj())) {
            throw new IllegalArgumentException("CNPJ já cadastrado no sistema");
        }

        if (organizacaoRepository.existsByEmail(organizacao.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado no sistema");
        }

        if (usuarioService.existsByEmail(organizacao.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado no sistema");
        }

        String senha = organizacao.getSenha();
        String senhaCriptografada = passwordEncoder.encode(senha);
        organizacao.setSenha(senhaCriptografada);
        return organizacaoRepository.save(organizacao);
    }
}
