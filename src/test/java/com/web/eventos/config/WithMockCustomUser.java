package com.web.eventos.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {
    int id() default 1;

    String nome() default "Usuário de Teste";

    String email() default "teste@mail.com";

    int avatarId() default 10;

    String avatarUrl() default "default-avatar.png";

    String tipo() default "USUARIO"; // "USUARIO" ou "ORGANIZACAO"

    String[] roles() default { "ROLE_USER" };
}
