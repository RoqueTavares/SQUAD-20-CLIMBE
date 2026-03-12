package com.squad20.sistema_climbe.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Entity
@Table(name = "tb_user")
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer id;

    @NotBlank(message = "O nome completo é obrigatório")
    @Column(name = "nome_completo", nullable = false)
    private String nomeCompleto;

    @Column(name = "cargo_id", nullable = false)
    private String cargoId;

    @NotBlank(message = "O CPF é obrigatório")
    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @NotBlank(message = "O e-mail é obrigatório")
    @Column(nullable = false, unique = true)
    private String email;

    @Column(length = 50)
    private String contato;

    private String situacao;

    @NotBlank(message = "A senha é obrigatória")
    @Column(name = "senha_hash", nullable = false, length = 60)
    private String senhaHash;
}
