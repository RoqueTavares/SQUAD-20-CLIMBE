package com.squad20.sistema_climbe.domain.document.controller;

import com.squad20.sistema_climbe.domain.document.dto.DocumentRequirementCreateRequest;
import com.squad20.sistema_climbe.domain.document.dto.DocumentRequirementDTO;
import com.squad20.sistema_climbe.domain.document.dto.DocumentRequirementPatchRequest;
import com.squad20.sistema_climbe.domain.document.service.DocumentRequirementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Requisitos Documentais", description = "Checklist documental por proposta")
@RestController
@RequiredArgsConstructor
public class DocumentRequirementController {

    private final DocumentRequirementService documentRequirementService;

    @Operation(summary = "Criar checklist documental", description = "Cria os requisitos documentais para a proposta informada")
    @PostMapping("/api/proposals/{proposalId}/documents/requirements")
    public ResponseEntity<List<DocumentRequirementDTO>> createRequirements(
            @Parameter(description = "ID da proposta") @PathVariable Long proposalId,
            @Valid @RequestBody(required = false) DocumentRequirementCreateRequest request) {
        DocumentRequirementCreateRequest safeRequest = request == null ? new DocumentRequirementCreateRequest() : request;
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(documentRequirementService.createRequirements(proposalId, safeRequest));
    }

    @Operation(summary = "Listar checklist documental", description = "Lista os requisitos documentais da proposta informada")
    @GetMapping("/api/proposals/{proposalId}/documents/requirements")
    public ResponseEntity<List<DocumentRequirementDTO>> listByProposal(
            @Parameter(description = "ID da proposta") @PathVariable Long proposalId) {
        return ResponseEntity.ok(documentRequirementService.listByProposal(proposalId));
    }

    @Operation(summary = "Atualizar requisito documental", description = "Atualiza parcialmente um requisito documental")
    @PatchMapping("/api/documents/requirements/{id}")
    public ResponseEntity<DocumentRequirementDTO> patchRequirement(
            @Parameter(description = "ID do requisito documental") @PathVariable Long id,
            @Valid @RequestBody DocumentRequirementPatchRequest patch) {
        return ResponseEntity.ok(documentRequirementService.patchRequirement(id, patch));
    }
}
