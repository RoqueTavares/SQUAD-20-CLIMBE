package com.squad20.sistema_climbe.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Entity
@Table(name = "tb_user")
@Data
public class User implements Serializable {

    private final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String nome_completo;
    private String cargo;
    private String cpf;
    private String email;
    private String contato;
    private String situacao;
    private String senha_hash;
}
