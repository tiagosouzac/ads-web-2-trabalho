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

        // Upload do avatar se fornecido
        if (avatar != null && !avatar.isEmpty()) {
            Midia avatarMidia = midiaService.uploadArquivo(avatar);
            usuario.setAvatar(avatarMidia);
        }

        String senha = usuario.getSenha();
        String senhaCriptografada = passwordEncoder.encode(senha);
        usuario.setSenha(senhaCriptografada);
        return usuarioRepository.save(usuario);
    }

    /**
     * Atualiza o avatar de um usuário
     * 
     * @param usuarioId ID do usuário
     * @param avatar    Novo arquivo de imagem do avatar
     * @return Usuario atualizado
     * @throws IOException Se houver erro ao fazer upload do avatar
     */
    public Usuario atualizarAvatar(Integer usuarioId, MultipartFile avatar) throws IOException {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        // Remover avatar antigo se existir
        if (usuario.getAvatar() != null) {
            midiaService.removerMidia(usuario.getAvatar().getId());
        }

        // Upload do novo avatar
        Midia novoAvatar = midiaService.uploadArquivo(avatar);
        usuario.setAvatar(novoAvatar);

        return usuarioRepository.save(usuario);
    }
}
