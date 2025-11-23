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

    public Organizacao findById(Integer id) {
        return organizacaoRepository.findById(id).orElse(null);
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

    public Organizacao atualizar(Organizacao organizacao, MultipartFile logo) throws IOException {
        Organizacao existing = organizacaoRepository.findById(organizacao.getId())
                .orElseThrow(() -> new IllegalArgumentException("Organização não encontrada"));

        // Check if email changed and if new email is taken by another organization or
        // user
        if (!existing.getEmail().equals(organizacao.getEmail())
                && (organizacaoRepository.existsByEmail(organizacao.getEmail())
                        || usuarioService.existsByEmail(organizacao.getEmail()))) {
            throw new IllegalArgumentException("E-mail já cadastrado no sistema");
        }

        // Update fields
        existing.setNome(organizacao.getNome());
        existing.setTipo(organizacao.getTipo());
        existing.setCnpj(organizacao.getCnpj());
        existing.setEmail(organizacao.getEmail());
        existing.setTelefone(organizacao.getTelefone());
        existing.setEndereco(organizacao.getEndereco());

        if (organizacao.getSenha() != null && !organizacao.getSenha().isEmpty()) {
            String senhaCriptografada = passwordEncoder.encode(organizacao.getSenha());
            existing.setSenha(senhaCriptografada);
        }

        if (logo != null && !logo.isEmpty()) {
            Midia logoMidia = midiaService.uploadArquivo(logo);
            existing.setLogo(logoMidia);
        }

        return organizacaoRepository.save(existing);
    }

    public void excluir(Integer id) {
        organizacaoRepository.deleteById(id);
    }
}
