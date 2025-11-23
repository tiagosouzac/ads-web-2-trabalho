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

    public Usuario findById(Integer id) {
        return usuarioRepository.findById(id).orElse(null);
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

    public Usuario atualizar(Usuario usuario, MultipartFile avatar) throws IOException {
        Usuario existing = usuarioRepository.findById(usuario.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        // Check if email changed and if new email is taken by another user
        if (!existing.getEmail().equals(usuario.getEmail()) && (usuarioRepository.existsByEmail(usuario.getEmail())
                || organizacaoService.existsByEmail(usuario.getEmail()))) {
            throw new IllegalArgumentException("E-mail já cadastrado no sistema");
        }

        // Update fields
        existing.setNome(usuario.getNome());
        existing.setCpf(usuario.getCpf());
        existing.setEmail(usuario.getEmail());

        if (usuario.getSenha() != null && !usuario.getSenha().isEmpty()) {
            String senhaCriptografada = passwordEncoder.encode(usuario.getSenha());
            existing.setSenha(senhaCriptografada);
        }

        if (avatar != null && !avatar.isEmpty()) {
            Midia avatarMidia = midiaService.uploadArquivo(avatar);
            existing.setAvatar(avatarMidia);
        }

        return usuarioRepository.save(existing);
    }
}
