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

    @Column(name = "razao_social")
    private String legalName;

    @Column(name = "nome_fantasia")
    private String tradeName;

    private String cnpj;

    @Column(name = "logradouro")
    private String street;

    @Column(name = "numero")
    private int number;

    @Column(name = "bairro")
    private String neighborhood;

    @Column(name = "cidade")
    private String city;

    @Column(name = "uf")
    private String state;

    @Column(name = "cep")
    private String zipCode;

    @Column(name = "telefone")
    private String phone;

    private String email;

    @Column(name = "representante_nome")
    private String representativeName;

    @Column(name = "representante_cnpj")
    private String representativeCpf;

    @Column(name = "representante_contato")
    private String representativePhone;
}
