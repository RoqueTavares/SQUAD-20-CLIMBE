package com.squad20.sistema_climbe.domain.document.entity;

import com.squad20.sistema_climbe.domain.common.entity.BaseEntity;
import com.squad20.sistema_climbe.domain.proposal.entity.Proposal;
import com.squad20.sistema_climbe.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SQLRestriction("deleted_at IS NULL")
@Entity
@Table(name = "requisitos_documentais")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentRequirement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_requisito_documental")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposta_id", nullable = false)
    private Proposal proposal;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false, length = 100)
    private DocumentType documentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private DocumentRequirementStatus status;

    @Column(name = "prazo")
    private LocalDate deadline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "documento_id")
    private Document document;

    @Column(name = "motivo_reprovacao", length = 2000)
    private String rejectionReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validado_por_id")
    private User validatedBy;

    @Column(name = "validado_em")
    private LocalDateTime validatedAt;

    @PrePersist
    protected void prePersist() {
        if (this.status == null) {
            this.status = DocumentRequirementStatus.PENDING;
        }
    }
}
