package com.squad20.sistema_climbe.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "servicos")
@Data
public class OfferedService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servico")
    private Long id;

    @Column(name = "nome", nullable = false, length = 255)
    private String name;
}

