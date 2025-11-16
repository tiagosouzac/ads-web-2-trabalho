package com.web.eventos.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "local")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Local {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "O nome do local é obrigatório")
    @Size(max = 255, message = "O nome deve ter no máximo 255 caracteres")
    @Column(nullable = false, length = 255)
    private String nome;

    @Size(max = 255, message = "O endereço deve ter no máximo 255 caracteres")
    @Column(length = 255)
    private String endereco;

    @Size(max = 100, message = "A cidade deve ter no máximo 100 caracteres")
    @Column(length = 100)
    private String cidade;

    @Size(max = 100, message = "O estado deve ter no máximo 100 caracteres")
    @Column(length = 100)
    private String estado;

    @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "CEP deve estar no formato 00000-000")
    @Column(length = 10)
    private String cep;

    @NotNull(message = "A capacidade é obrigatória")
    @Positive(message = "A capacidade deve ser um número positivo")
    @Column(nullable = false)
    private Integer capacidade;

    @Column(columnDefinition = "TEXT")
    private String facilidades;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;
}
