package com.web.eventos.config;

import com.web.eventos.entities.Midia;
import com.web.eventos.security.CustomUserDetails;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory
        implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Midia mockAvatar = Midia.builder()
                .id(customUser.avatarId())
                .url(customUser.avatarUrl())
                .build();

        CustomUserDetails principal = new CustomUserDetails(
                customUser.id(),
                customUser.email(),
                "password",
                customUser.nome(),
                mockAvatar,
                customUser.tipo(),
                AuthorityUtils.createAuthorityList(customUser.roles()));

        Authentication auth = new UsernamePasswordAuthenticationToken(
                principal,
                principal.getPassword(),
                principal.getAuthorities());

        context.setAuthentication(auth);
        return context;
    }
}