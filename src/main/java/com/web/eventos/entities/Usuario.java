package com.web.eventos.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 255, message = "O nome deve ter no máximo 255 caracteres")
    @Column(nullable = false, length = 255)
    private String nome;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Size(max = 255, message = "O email deve ter no máximo 255 caracteres")
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(length = 255, nullable = false)
    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
    private String senha;

    @NotBlank(message = "O CPF é obrigatório")
    @Pattern(regexp = "^\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}$", message = "CPF deve estar no formato 000.000.000-00")
    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @OneToOne
    @JoinColumn(name = "avatar_id")
    private Midia avatar;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;
}
