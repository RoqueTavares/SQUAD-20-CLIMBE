package com.squad20.sistema_climbe.domain.document.service;

import com.squad20.sistema_climbe.domain.document.dto.DocumentRequirementCreateRequest;
import com.squad20.sistema_climbe.domain.document.dto.DocumentRequirementDTO;
import com.squad20.sistema_climbe.domain.document.dto.DocumentRequirementPatchRequest;
import com.squad20.sistema_climbe.domain.document.entity.DocumentRequirement;
import com.squad20.sistema_climbe.domain.document.entity.DocumentRequirementStatus;
import com.squad20.sistema_climbe.domain.document.entity.DocumentType;
import com.squad20.sistema_climbe.domain.document.mapper.DocumentRequirementMapper;
import com.squad20.sistema_climbe.domain.document.repository.DocumentRequirementRepository;
import com.squad20.sistema_climbe.domain.proposal.entity.Proposal;
import com.squad20.sistema_climbe.domain.proposal.repository.ProposalRepository;
import com.squad20.sistema_climbe.domain.user.entity.User;
import com.squad20.sistema_climbe.domain.user.repository.UserRepository;
import com.squad20.sistema_climbe.exception.BadRequestException;
import com.squad20.sistema_climbe.exception.ConflictException;
import com.squad20.sistema_climbe.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DocumentRequirementService {

    private final DocumentRequirementRepository documentRequirementRepository;
    private final ProposalRepository proposalRepository;
    private final UserRepository userRepository;
    private final DocumentRequirementMapper documentRequirementMapper;

    @Transactional
    public List<DocumentRequirementDTO> createRequirements(Long proposalId, DocumentRequirementCreateRequest request) {
        Proposal proposal = findProposalOrThrow(proposalId);

        List<DocumentType> typesToCreate = normalizeTypes(request != null ? request.getDocumentTypes() : null);
        validateNoDuplicateInProposal(proposalId, typesToCreate);

        List<DocumentRequirement> created = typesToCreate.stream()
                .map(type -> DocumentRequirement.builder()
                        .proposal(proposal)
                        .documentType(type)
                        .status(DocumentRequirementStatus.PENDING)
                        .deadline(request != null ? request.getDeadline() : null)
                        .build())
                .toList();

        return documentRequirementRepository.saveAll(created).stream()
                .map(documentRequirementMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DocumentRequirementDTO> listByProposal(Long proposalId) {
        findProposalOrThrow(proposalId);
        return documentRequirementRepository.findByProposal_Id(proposalId).stream()
                .map(documentRequirementMapper::toDTO)
                .toList();
    }

    @Transactional
    public DocumentRequirementDTO patchRequirement(Long id, DocumentRequirementPatchRequest patch) {
        DocumentRequirement requirement = documentRequirementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Requisito documental nao encontrado com id: " + id));

        if (patch.getDeadline() != null) {
            requirement.setDeadline(patch.getDeadline());
        }

        if (patch.getValidatedById() != null) {
            User validatedBy = userRepository.findById(patch.getValidatedById())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado com id: " + patch.getValidatedById()));
            requirement.setValidatedBy(validatedBy);
        }

        if (patch.getStatus() != null) {
            requirement.setStatus(patch.getStatus());

            if (patch.getStatus() == DocumentRequirementStatus.NON_COMPLIANT) {
                String reason = patch.getRejectionReason();
                if (reason == null || reason.isBlank()) {
                    throw new BadRequestException("rejectionReason e obrigatorio quando status for NON_COMPLIANT");
                }
                requirement.setRejectionReason(reason);
                requirement.setValidatedAt(LocalDateTime.now());
            } else if (patch.getStatus() == DocumentRequirementStatus.APPROVED) {
                requirement.setRejectionReason(null);
                requirement.setValidatedAt(LocalDateTime.now());
            } else if (patch.getRejectionReason() != null) {
                requirement.setRejectionReason(patch.getRejectionReason());
            }
        } else if (patch.getRejectionReason() != null) {
            requirement.setRejectionReason(patch.getRejectionReason());
        }

        return documentRequirementMapper.toDTO(documentRequirementRepository.save(requirement));
    }

    private Proposal findProposalOrThrow(Long proposalId) {
        return proposalRepository.findById(proposalId)
                .orElseThrow(() -> new ResourceNotFoundException("Proposta nao encontrada com id: " + proposalId));
    }

    private List<DocumentType> normalizeTypes(List<DocumentType> requestedTypes) {
        if (requestedTypes == null || requestedTypes.isEmpty()) {
            return Arrays.asList(DocumentType.values());
        }

        Set<DocumentType> seen = new HashSet<>();
        for (DocumentType type : requestedTypes) {
            if (type == null) {
                throw new BadRequestException("documentTypes nao pode conter itens nulos");
            }
            if (!seen.add(type)) {
                throw new BadRequestException("documentTypes nao pode conter tipos repetidos no payload");
            }
        }

        return requestedTypes;
    }

    private void validateNoDuplicateInProposal(Long proposalId, List<DocumentType> types) {
        for (DocumentType type : types) {
            if (documentRequirementRepository.existsByProposal_IdAndDocumentType(proposalId, type)) {
                throw new ConflictException("Ja existe requisito documental ativo para proposta "
                        + proposalId + " e tipo " + type);
            }
        }
    }
}
