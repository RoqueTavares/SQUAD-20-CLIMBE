package com.squad20.sistema_climbe.domain.document.dto;

import com.squad20.sistema_climbe.domain.document.entity.DocumentRequirementStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentRequirementPatchRequest {

    private DocumentRequirementStatus status;

    private LocalDate deadline;

    private String rejectionReason;

    private Long validatedById;
}
