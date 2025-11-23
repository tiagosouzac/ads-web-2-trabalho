package com.web.eventos.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.web.eventos.entities.Midia;
import com.web.eventos.entities.Usuario;
import com.web.eventos.repositories.UsuarioRepository;

import java.io.IOException;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final OrganizacaoService organizacaoService;
    private final PasswordEncoder passwordEncoder;
    private final MidiaService midiaService;

    public UsuarioService(UsuarioRepository usuarioRepository, OrganizacaoService organizacaoService,
            PasswordEncoder passwordEncoder, MidiaService midiaService) {
        this.usuarioRepository = usuarioRepository;
        this.organizacaoService = organizacaoService;
        this.passwordEncoder = passwordEncoder;
        this.midiaService = midiaService;
    }

    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public Usuario salvar(Usuario usuario, MultipartFile avatar) throws IOException {
        if (usuarioRepository.existsByCpf(usuario.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado no sistema");
        }

        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado no sistema");
        }

        if (organizacaoService.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado no sistema");
        }

        if (avatar != null && !avatar.isEmpty()) {
            Midia avatarMidia = midiaService.uploadArquivo(avatar);
            usuario.setAvatar(avatarMidia);
        }

        String senha = usuario.getSenha();
        String senhaCriptografada = passwordEncoder.encode(senha);
        usuario.setSenha(senhaCriptografada);
        return usuarioRepository.save(usuario);
    }
}
