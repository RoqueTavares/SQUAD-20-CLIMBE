package com.squad20.sistema_climbe.domain.contract.service;

import com.squad20.sistema_climbe.domain.contract.dto.ContractCreateRequest;
import com.squad20.sistema_climbe.domain.contract.dto.ContractDTO;
import com.squad20.sistema_climbe.domain.contract.dto.ContractPatchRequest;
import com.squad20.sistema_climbe.domain.contract.entity.Contract;
import com.squad20.sistema_climbe.domain.contract.mapper.ContractMapper;
import com.squad20.sistema_climbe.domain.contract.repository.ContractRepository;
import com.squad20.sistema_climbe.domain.proposal.entity.Proposal;
import com.squad20.sistema_climbe.domain.proposal.entity.ProposalStatus;
import com.squad20.sistema_climbe.domain.proposal.repository.ProposalRepository;
import com.squad20.sistema_climbe.domain.proposal.service.ProposalService;
import com.squad20.sistema_climbe.domain.report.repository.ReportRepository;
import com.squad20.sistema_climbe.domain.spreadsheet.repository.SpreadsheetRepository;
import com.squad20.sistema_climbe.exception.BadRequestException;
import com.squad20.sistema_climbe.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.squad20.sistema_climbe.domain.contract.entity.ContractTeam;
import com.squad20.sistema_climbe.domain.contract.repository.ContractTeamRepository;
import com.squad20.sistema_climbe.domain.user.entity.User;
import com.squad20.sistema_climbe.domain.user.repository.UserRepository;
import com.squad20.sistema_climbe.domain.notification.service.NotificationService;
import com.squad20.sistema_climbe.domain.notification.dto.NotificationCreateRequest;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

import com.squad20.sistema_climbe.service.GoogleWorkspaceService;
import com.squad20.sistema_climbe.domain.spreadsheet.dto.SpreadsheetCreateRequest;
import com.squad20.sistema_climbe.domain.spreadsheet.service.SpreadsheetService;
import com.squad20.sistema_climbe.service.GoogleCloudStorageService;

@Service
@RequiredArgsConstructor
public class ContractService {

    private static final String DIGITALLY_SIGNED_STATUS = "DIGITALLY_SIGNED";

    private final ContractRepository contractRepository;
    private final ProposalRepository proposalRepository;
    private final ReportRepository reportRepository;
    private final SpreadsheetRepository spreadsheetRepository;
    private final ProposalService proposalService;
    private final ContractMapper contractMapper;
    private final ContractTeamRepository contractTeamRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final GoogleWorkspaceService googleWorkspaceService;
    private final SpreadsheetService spreadsheetService;
    private final PdfGeneratorService pdfGeneratorService;
    private final SignatureService signatureService;
    private final GoogleCloudStorageService googleCloudStorageService;

    @Transactional(readOnly = true)
    public Page<ContractDTO> findAll(Pageable pageable) {
        return contractRepository.findAll(pageable).map(contractMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public List<ContractDTO> findByProposalId(Long proposalId) {
        return contractRepository.findByProposal_Id(proposalId).stream()
                .map(contractMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ContractDTO findById(Long id) {
        Contract contract = findContractOrThrow(id);
        return contractMapper.toDTO(contract);
    }

    @Transactional(readOnly = true)
    public String generateViewUrl(Long id) {
        Contract contract = findContractOrThrow(id);
        if (contract.getPdfUrl() == null || contract.getPdfUrl().isEmpty()) {
            throw new ResourceNotFoundException("Este contrato não possui um PDF anexado.");
        }

        try {
            if (contract.getPdfUrl().startsWith("http")) {
                return contract.getPdfUrl();
            }
            return googleCloudStorageService.generateSignedUrl(contract.getPdfUrl());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar link seguro de visualização", e);
        }
    }

    @Transactional
    public ContractDTO save(ContractCreateRequest request) {
        Proposal proposal = findProposalOrThrow(request.getProposalId());
        validateProposalCanCreateContract(proposal);

        Contract contract = contractMapper.toEntity(request);
        contract.setId(null);
        contract.setProposal(proposal);
        contract = contractRepository.save(contract);

        try {
            // Gera o PDF
            byte[] pdfBytes = pdfGeneratorService.generateContractPdf(contract);
            
            // Faz upload para o Storage
            String fileName = "contrato_" + contract.getId() + ".pdf";
            String pdfUrl = googleCloudStorageService.uploadPrivateFileBytes(pdfBytes, fileName, "contratos", "application/pdf");
            contract.setPdfUrl(pdfUrl);
            
            // Simula envio para assinatura
            String signatureId = signatureService.sendDocumentForSignature(contract, pdfUrl);
            contract.setExternalSignatureId(signatureId);
            
            // Atualiza com URL e ID de assinatura
            contract = contractRepository.save(contract);
        } catch (Exception e) {
            System.err.println("Erro ao gerar/enviar PDF do contrato: " + e.getMessage());
            e.printStackTrace();
        }

        updateProposalIfContractWasSigned(contract);
        return contractMapper.toDTO(contract);
    }

    @Transactional
    public ContractDTO update(Long id, ContractPatchRequest patch) {
        Contract existing = findContractOrThrow(id);

        if (patch.getStartDate() != null) existing.setStartDate(patch.getStartDate());
        if (patch.getEndDate() != null) existing.setEndDate(patch.getEndDate());
        if (patch.getStatus() != null) existing.setStatus(patch.getStatus());

        if (patch.getProposalId() != null) {
            Proposal proposal = findProposalOrThrow(patch.getProposalId());
            validateProposalCanCreateContract(proposal);
            existing.setProposal(proposal);
        }

        existing = contractRepository.save(existing);
        updateProposalIfContractWasSigned(existing);
        return contractMapper.toDTO(existing);
    }

    @Transactional
    public ContractDTO setExecutionDeadline(Long id, LocalDate deadline) {
        Contract contract = findContractOrThrow(id);
        contract.setExecutionDeadline(deadline);
        contract = contractRepository.save(contract);
        return contractMapper.toDTO(contract);
    }

    @Transactional
    public void assignTeam(Long contractId, List<Long> userIds, String roleInTeam) {
        Contract contract = findContractOrThrow(contractId);
        List<ContractTeam> teamMembers = new ArrayList<>();
        for (Long userId : userIds) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + userId));
            
            ContractTeam teamMember = ContractTeam.builder()
                    .contract(contract)
                    .user(user)
                    .roleInTeam(roleInTeam)
                    .build();
            teamMembers.add(teamMember);
            
            notificationService.save(NotificationCreateRequest.builder()
                    .userId(user.getId())
                    .type("TEAM_ASSIGNMENT")
                    .message("Você foi alocado no contrato " + contract.getId() + " como " + roleInTeam)
                    .build());
        }
        contractTeamRepository.saveAll(teamMembers);
        
        unlockResourcesForContract(contract);
    }

    private void unlockResourcesForContract(Contract contract) {
        System.out.println("Iniciando desbloqueio de recursos GCP para o contrato " + contract.getId());
        try {
            User ceoUser = userRepository.findByRole(com.squad20.sistema_climbe.domain.user.entity.Role.CEO).stream()
                    .filter(u -> u.getGoogleRefreshToken() != null)
                    .findFirst()
                    .orElse(null);

            if (ceoUser != null) {
                List<String> teamEmails = contractTeamRepository.findByContract_Id(contract.getId()).stream()
                        .map(ct -> ct.getUser().getEmail())
                        .filter(email -> email != null && !email.isBlank())
                        .toList();

                String contractTitle = contract.getProposal().getEnterprise().getTradeName() != null ?
                        contract.getProposal().getEnterprise().getTradeName() :
                        contract.getProposal().getEnterprise().getLegalName();

                String spreadsheetLink = googleWorkspaceService.createContractEnvironment(
                        ceoUser.getGoogleRefreshToken(),
                        contractTitle + " - Contrato " + contract.getId(),
                        teamEmails
                );

                if (spreadsheetLink != null) {
                    spreadsheetService.save(SpreadsheetCreateRequest.builder()
                            .contractId(contract.getId())
                            .googleSheetsUrl(spreadsheetLink)
                            .locked(false)
                            .viewPermission("TEAM_ONLY")
                            .build());
                    System.out.println("Planilha criada e salva no banco de dados com link: " + spreadsheetLink);
                }
            } else {
                System.err.println("Nenhum CEO com token Google cadastrado para gerar os recursos de Workspace do Contrato " + contract.getId());
            }
        } catch (Exception e) {
            System.err.println("Erro ao integrar com Google Workspace para o contrato " + contract.getId() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Transactional
    public void delete(Long id) {
        Contract contract = findContractOrThrow(id);
        LocalDateTime now = LocalDateTime.now();
        reportRepository.softDeleteByContractId(id, now);
        spreadsheetRepository.softDeleteByContractId(id, now);

        contract.setDeletedAt(now);
        contractRepository.save(contract);
    }

    private Proposal findProposalOrThrow(Long proposalId) {
        return proposalRepository.findById(proposalId)
                .orElseThrow(() -> new ResourceNotFoundException("Proposta não encontrada com id: " + proposalId));
    }

    private void validateProposalCanCreateContract(Proposal proposal) {
        ProposalStatus status = proposal.getStatus();
        if (status == null) {
            throw new BadRequestException("A proposta vinculada ao contrato não possui um status válido.");
        }
        if (status != ProposalStatus.COMMERCIAL_PROPOSAL_APPROVED) {
            throw new BadRequestException(
                    "O contrato só pode ser criado quando a proposta comercial estiver aprovada.");
        }
    }

    private void updateProposalIfContractWasSigned(Contract contract) {
        if (isDigitallySigned(contract.getStatus())) {
            proposalService.markReadyForNextStage(contract.getProposal().getId());
        }
    }

    private boolean isDigitallySigned(String status) {
        return status != null && DIGITALLY_SIGNED_STATUS.equals(status.trim().toUpperCase(Locale.ROOT));
    }

    private Contract findContractOrThrow(Long id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrato não encontrado com id: " + id));
    }
}
