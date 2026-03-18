package com.squad20.sistema_climbe.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "contratos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contrato")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposta_id", nullable = false)
    private Proposal proposal;

    @Column(name = "data_inicio")
    private LocalDate startDate;

    @Column(name = "data_fim")
    private LocalDate endDate;

    @Column(name = "status", length = 50)
    private String status;
}
