package com.squad20.sistema_climbe.domain.document.service;

import com.squad20.sistema_climbe.domain.document.dto.DocumentRequirementCreateRequest;
import com.squad20.sistema_climbe.domain.document.dto.DocumentRequirementDTO;
import com.squad20.sistema_climbe.domain.document.dto.DocumentRequirementPatchRequest;
import com.squad20.sistema_climbe.domain.document.entity.DocumentRequirement;
import com.squad20.sistema_climbe.domain.document.entity.DocumentRequirementStatus;
import com.squad20.sistema_climbe.domain.document.entity.DocumentType;
import com.squad20.sistema_climbe.domain.document.mapper.DocumentRequirementMapper;
import com.squad20.sistema_climbe.domain.document.repository.DocumentRequirementRepository;
import com.squad20.sistema_climbe.domain.enterprise.entity.Enterprise;
import com.squad20.sistema_climbe.domain.proposal.entity.Proposal;
import com.squad20.sistema_climbe.domain.proposal.repository.ProposalRepository;
import com.squad20.sistema_climbe.domain.user.entity.User;
import com.squad20.sistema_climbe.domain.user.repository.UserRepository;
import com.squad20.sistema_climbe.exception.BadRequestException;
import com.squad20.sistema_climbe.exception.ConflictException;
import com.squad20.sistema_climbe.exception.ResourceNotFoundException;
import com.squad20.sistema_climbe.domain.notification.dto.NotificationCreateRequest;
import com.squad20.sistema_climbe.domain.notification.service.NotificationService;
import com.squad20.sistema_climbe.messaging.EmailMessage;
import com.squad20.sistema_climbe.messaging.EmailPublisher;
import com.squad20.sistema_climbe.messaging.EmailRoutingKeys;
import com.squad20.sistema_climbe.domain.user.entity.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentRequirementService {

    private final DocumentRequirementRepository documentRequirementRepository;
    private final ProposalRepository proposalRepository;
    private final UserRepository userRepository;
    private final DocumentRequirementMapper documentRequirementMapper;
    private final EmailPublisher emailPublisher;
    private final NotificationService notificationService;

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

        List<DocumentRequirementDTO> result = documentRequirementRepository.saveAll(created).stream()
                .map(documentRequirementMapper::toDTO)
                .toList();

      
        notifyEnterpriseDocumentsRequested(proposal, typesToCreate);

        return result;
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

        DocumentRequirementStatus previousStatus = requirement.getStatus();

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

        DocumentRequirement saved = documentRequirementRepository.save(requirement);

       
        if (patch.getStatus() != null && patch.getStatus() != previousStatus) {
            if (patch.getStatus() == DocumentRequirementStatus.NON_COMPLIANT) {
                notifyEnterpriseDocumentNonCompliant(saved);
            } else if (patch.getStatus() == DocumentRequirementStatus.APPROVED) {
                notifyEnterpriseDocumentApproved(saved);
                if (checkIfAllDocumentsApproved(saved.getProposal().getId())) {
                    notifySeniorAnalystsForDeadline(saved.getProposal());
                }
            }
        }

        return documentRequirementMapper.toDTO(saved);
    }

   
    private void notifyEnterpriseDocumentsRequested(Proposal proposal, List<DocumentType> types) {
        Enterprise enterprise = proposal.getEnterprise();
        if (enterprise == null || !hasText(enterprise.getEmail())) {
            log.warn("Proposta {} sem e-mail de empresa cadastrado; notificação de solicitação de documentos não enviada.", proposal.getId());
            return;
        }

        String companyName = hasText(enterprise.getTradeName()) ? enterprise.getTradeName() : enterprise.getLegalName();

        String typesList = types.stream()
                .map(DocumentType::name)
                .collect(Collectors.joining("\n  - ", "  - ", ""));

        String body = String.format(
                "Olá %s,%n%n" +
                "A Climbe solicita o envio dos seguintes documentos referentes à proposta #%d:%n%n" +
                "%s%n%n" +
                "Por favor, acesse o portal e faça o upload de cada documento no prazo estipulado.%n%n" +
                "Equipe Climbe",
                companyName,
                proposal.getId(),
                typesList
        );

        emailPublisher.publish(
                EmailRoutingKeys.DOCUMENT_REQUESTED,
                EmailMessage.builder()
                        .to(enterprise.getEmail())
                        .subject("Documentação necessária — Proposta #" + proposal.getId() + " | Sistema Climbe")
                        .body(body)
                        .build()
        );
    }

    
    private void notifyEnterpriseDocumentNonCompliant(DocumentRequirement requirement) {
        Enterprise enterprise = requirement.getProposal().getEnterprise();
        if (enterprise == null || !hasText(enterprise.getEmail())) {
            log.warn("Requisito documental {} sem e-mail de empresa; notificação de não conformidade não enviada.", requirement.getId());
            return;
        }

        String companyName = hasText(enterprise.getTradeName()) ? enterprise.getTradeName() : enterprise.getLegalName();

        String body = String.format(
                "Olá %s,%n%n" +
                "O documento do tipo %s enviado para a proposta #%d foi analisado e considerado NÃO CONFORME.%n%n" +
                "Motivo: %s%n%n" +
                "Por favor, corrija e reenvie o documento o mais breve possível.%n%n" +
                "Equipe Climbe",
                companyName,
                requirement.getDocumentType().name(),
                requirement.getProposal().getId(),
                requirement.getRejectionReason()
        );

        emailPublisher.publish(
                EmailRoutingKeys.DOCUMENT_VALIDATION_RESULT,
                EmailMessage.builder()
                        .to(enterprise.getEmail())
                        .subject("Documento não conforme — " + requirement.getDocumentType().name() + " | Proposta #" + requirement.getProposal().getId())
                        .body(body)
                        .build()
        );
    }

    
    private void notifyEnterpriseDocumentApproved(DocumentRequirement requirement) {
        Enterprise enterprise = requirement.getProposal().getEnterprise();
        if (enterprise == null || !hasText(enterprise.getEmail())) {
            log.warn("Requisito documental {} sem e-mail de empresa; notificação de aprovação não enviada.", requirement.getId());
            return;
        }

        String companyName = hasText(enterprise.getTradeName()) ? enterprise.getTradeName() : enterprise.getLegalName();

        String body = String.format(
                "Olá %s,%n%n" +
                "O documento do tipo %s referente à proposta #%d foi analisado e está EM CONFORMIDADE.%n%n" +
                "Obrigado pela colaboração.%n%n" +
                "Equipe Climbe",
                companyName,
                requirement.getDocumentType().name(),
                requirement.getProposal().getId()
        );

        emailPublisher.publish(
                EmailRoutingKeys.DOCUMENT_VALIDATION_RESULT,
                EmailMessage.builder()
                        .to(enterprise.getEmail())
                        .subject("Documento aprovado — " + requirement.getDocumentType().name() + " | Proposta #" + requirement.getProposal().getId())
                        .body(body)
                        .build()
        );
    }

    private boolean checkIfAllDocumentsApproved(Long proposalId) {
        List<DocumentRequirement> requirements = documentRequirementRepository.findByProposal_Id(proposalId);
        if (requirements.isEmpty()) return false;
        return requirements.stream().allMatch(req -> req.getStatus() == DocumentRequirementStatus.APPROVED);
    }

    private void notifySeniorAnalystsForDeadline(Proposal proposal) {
        List<User> seniors = userRepository.findByRole(Role.ANALISTA_SENIOR);
        for (User senior : seniors) {
            notificationService.save(NotificationCreateRequest.builder()
                    .userId(senior.getId())
                    .type("ALL_DOCUMENTS_APPROVED")
                    .message("Todos os documentos da proposta " + proposal.getId() + " foram aprovados. Por favor, defina o prazo de execução no contrato.")
                    .build());
        }
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

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
