package com.web.eventos.services;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.web.eventos.entities.Midia;
import com.web.eventos.entities.Organizacao;
import com.web.eventos.repositories.OrganizacaoRepository;

import java.io.IOException;

@Service
public class OrganizacaoService {
    private final OrganizacaoRepository organizacaoRepository;
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final MidiaService midiaService;

    public OrganizacaoService(OrganizacaoRepository organizacaoRepository, @Lazy UsuarioService usuarioService,
            PasswordEncoder passwordEncoder, MidiaService midiaService) {
        this.organizacaoRepository = organizacaoRepository;
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
        this.midiaService = midiaService;
    }

    public Organizacao findByEmail(String email) {
        return organizacaoRepository.findByEmail(email).orElse(null);
    }

    public boolean existsByEmail(String email) {
        return organizacaoRepository.existsByEmail(email);
    }

    public Organizacao salvar(Organizacao organizacao, MultipartFile logo) throws IOException {
        if (organizacaoRepository.existsByCnpj(organizacao.getCnpj())) {
            throw new IllegalArgumentException("CNPJ já cadastrado no sistema");
        }

        if (organizacaoRepository.existsByEmail(organizacao.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado no sistema");
        }

        if (usuarioService.existsByEmail(organizacao.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado no sistema");
        }

        if (logo != null && !logo.isEmpty()) {
            Midia logoMidia = midiaService.uploadArquivo(logo);
            organizacao.setLogo(logoMidia);
        }

        String senha = organizacao.getSenha();
        String senhaCriptografada = passwordEncoder.encode(senha);
        organizacao.setSenha(senhaCriptografada);
        return organizacaoRepository.save(organizacao);
    }
}
