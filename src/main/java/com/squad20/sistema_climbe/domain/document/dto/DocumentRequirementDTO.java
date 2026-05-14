package com.squad20.sistema_climbe.domain.document.dto;

import com.squad20.sistema_climbe.domain.document.entity.DocumentRequirementStatus;
import com.squad20.sistema_climbe.domain.document.entity.DocumentType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentRequirementDTO {

    private Long id;

    private Long proposalId;

    private DocumentType documentType;

    private DocumentRequirementStatus status;

    private LocalDate deadline;

    private Long documentId;

    private String rejectionReason;

    private Long validatedById;

    private LocalDateTime validatedAt;

    private LocalDateTime createdAt;
}
