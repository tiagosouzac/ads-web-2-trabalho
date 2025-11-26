package com.web.eventos.services;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.web.eventos.entities.Organizacao;
import com.web.eventos.entities.Usuario;
import com.web.eventos.security.CustomUserDetails;

@Service
public class AutenticacaoService implements UserDetailsService {
    private final UsuarioService usuarioService;
    private final OrganizacaoService organizacaoService;

    public AutenticacaoService(UsuarioService usuarioService, OrganizacaoService organizacaoService) {
        this.usuarioService = usuarioService;
        this.organizacaoService = organizacaoService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioService.findByEmail(email);

        if (usuario != null) {
            Set<GrantedAuthority> authorities = new HashSet<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + usuario.getRole().name()));

            return new CustomUserDetails(
                    usuario.getId(),
                    email,
                    usuario.getSenha(),
                    usuario.getNome(),
                    usuario.getAvatar(),
                    "USUARIO",
                    authorities);
        }

        Organizacao organizacao = organizacaoService.findByEmail(email);

        if (organizacao != null) {
            Set<GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_ORGANIZACAO"));

            return new CustomUserDetails(
                    organizacao.getId(),
                    email,
                    organizacao.getSenha(),
                    organizacao.getNome(),
                    organizacao.getLogo(),
                    "ORGANIZACAO",
                    authorities);
        }

        throw new UsernameNotFoundException("Usuário ou organização não encontrado com o e-mail: " + email);
    }

    public void atualizarContextoAutenticacao(String email) {
        UserDetails userDetails = loadUserByUsername(email);

        Authentication authenticacao = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
                userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authenticacao);
    }
}
