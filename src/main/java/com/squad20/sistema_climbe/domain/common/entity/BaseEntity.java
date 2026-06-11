package com.squad20.sistema_climbe.domain.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    // Preenchido automaticamente ao persistir; nunca atualizado
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Preenchido automaticamente ao atualizar
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Auditoria de quem criou
    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private Long createdBy;

    // Auditoria de quem atualizou
    @LastModifiedBy
    @Column(name = "updated_by")
    private Long updatedBy;

    // NULL = ativo. Não-NULL = deletado. Definido pelo service ao deletar.
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public boolean isDeleted() {
        return deletedAt != null;
    }
}
