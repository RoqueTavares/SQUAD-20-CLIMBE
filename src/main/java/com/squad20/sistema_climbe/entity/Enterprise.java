package com.squad20.sistema_climbe.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Entity
@Table(name = "tb_enterprise")
@Data
public class Enterprise implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String razao_social;
    private String nome_fantasia;
    private String cnpj;
    private String logradouro;
    private int numero;
    private String bairro;
    private String cidade;
    private String uf;
    private String cep;
    private String telefone;
    private String email;
    private String representante_nome;
    private String representante_cnpj;
    private String representante_contato;
}
