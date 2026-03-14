package com.squad20.sistema_climbe.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @Column(name = "nome_completo", nullable = false, length = 255)
    private String fullName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cargo_id", nullable = false)
    private Cargo role;

    @Column(nullable = false, length = 14)
    private String cpf;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(name = "contato", length = 50)
    private String phone;

    @Column(name = "situacao", length = 255)
    private String status;

    @Column(name = "senha_hash", length = 60)
    private String passwordHash;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "usuario_permissoes",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_permissao")
    )
    private Set<Permission> permissions;
}

