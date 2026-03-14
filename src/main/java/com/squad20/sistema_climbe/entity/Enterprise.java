package com.squad20.sistema_climbe.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "empresas")
@Data
public class Enterprise implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empresa")
    private Long id;

    @Column(name = "razao_social")
    private String legalName;

    @Column(name = "nome_fantasia")
    private String tradeName;

    private String cnpj;

    @Column(name = "logradouro")
    private String street;

    @Column(name = "numero", length = 255)
    private String number;

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

    @Column(name = "representante_cpf", length = 14)
    private String representativeCpf;

    @Column(name = "representante_contato")
    private String representativePhone;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "empresa_servico",
            joinColumns = @JoinColumn(name = "id_empresa"),
            inverseJoinColumns = @JoinColumn(name = "id_servico")
    )
    private Set<OfferedService> services;
}

