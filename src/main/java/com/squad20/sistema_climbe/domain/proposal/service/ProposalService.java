package com.squad20.sistema_climbe.domain.proposal.service;

import com.squad20.sistema_climbe.domain.contract.repository.ContractRepository;
import com.squad20.sistema_climbe.domain.enterprise.entity.Address;
import com.squad20.sistema_climbe.domain.enterprise.entity.Enterprise;
import com.squad20.sistema_climbe.domain.enterprise.repository.EnterpriseRepository;
import com.squad20.sistema_climbe.domain.proposal.dto.ProposalCreateRequest;
import com.squad20.sistema_climbe.domain.proposal.dto.ProposalDTO;
import com.squad20.sistema_climbe.domain.proposal.dto.ProposalPatchRequest;
import com.squad20.sistema_climbe.domain.proposal.entity.Proposal;
import com.squad20.sistema_climbe.domain.proposal.entity.ProposalStatus;
import com.squad20.sistema_climbe.domain.proposal.mapper.ProposalMapper;
import com.squad20.sistema_climbe.domain.proposal.repository.ProposalRepository;
import com.squad20.sistema_climbe.domain.report.repository.ReportRepository;
import com.squad20.sistema_climbe.domain.spreadsheet.repository.SpreadsheetRepository;
import com.squad20.sistema_climbe.domain.user.entity.User;
import com.squad20.sistema_climbe.domain.user.repository.UserRepository;
import com.squad20.sistema_climbe.exception.BadRequestException;
import com.squad20.sistema_climbe.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProposalService {

    private final ProposalRepository proposalRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final UserRepository userRepository;
    private final ContractRepository contractRepository;
    private final ReportRepository reportRepository;
    private final SpreadsheetRepository spreadsheetRepository;
    private final ProposalMapper proposalMapper;

    @Transactional(readOnly = true)
    public Page<ProposalDTO> findAll(Pageable pageable) {
        return proposalRepository.findAll(pageable).map(proposalMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public List<ProposalDTO> findByEnterpriseId(Long enterpriseId) {
        return proposalRepository.findByEnterprise_Id(enterpriseId).stream()
                .map(proposalMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProposalDTO> findByUserId(Long userId) {
        return proposalRepository.findByUser_Id(userId).stream()
                .map(proposalMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProposalDTO findById(Long id) {
        Proposal proposal = findProposalOrThrow(id);
        return proposalMapper.toDTO(proposal);
    }

    @Transactional
    public ProposalDTO save(ProposalCreateRequest request) {
        Enterprise enterprise = enterpriseRepository.findById(request.getEnterpriseId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada com id: " + request.getEnterpriseId()));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + request.getUserId()));

        Proposal proposal = proposalMapper.toEntity(request);
        proposal.setId(null);
        proposal.setEnterprise(enterprise);
        proposal.setUser(user);
        proposal.setStatus(ProposalStatus.RECEIVED.name());

        proposal = proposalRepository.save(proposal);
        return proposalMapper.toDTO(proposal);
    }

    @Transactional
    public ProposalDTO update(Long id, ProposalPatchRequest patch) {
        Proposal existing = findProposalOrThrow(id);

        if (patch.getEnterpriseId() != null) {
            Enterprise enterprise = enterpriseRepository.findById(patch.getEnterpriseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada com id: " + patch.getEnterpriseId()));
            existing.setEnterprise(enterprise);
        }

        if (patch.getUserId() != null) {
            User user = userRepository.findById(patch.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + patch.getUserId()));
            existing.setUser(user);
        }

        if (patch.getStatus() != null) {
            applyTriageStatus(existing, patch.getStatus());
        }

        existing = proposalRepository.save(existing);
        return proposalMapper.toDTO(existing);
    }

    @Transactional
    public void delete(Long id) {
        Proposal proposal = findProposalOrThrow(id);
        // Cascade soft delete: deletar proposta propaga para todos os registros associados.
        // Ordem importa: filhos antes do pai para evitar inconsistências.
        LocalDateTime now = LocalDateTime.now();
        reportRepository.softDeleteByProposalId(id, now);
        spreadsheetRepository.softDeleteByProposalId(id, now);
        contractRepository.softDeleteByProposalId(id, now);

        proposal.setDeletedAt(now);
        proposalRepository.save(proposal);
    }

    private Proposal findProposalOrThrow(Long id) {
        return proposalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proposta não encontrada com id: " + id));
    }

    private void applyTriageStatus(Proposal proposal, ProposalStatus newStatus) {
        ProposalStatus currentStatus = normalizeTriageStatus(proposal.getStatus());

        if (currentStatus == newStatus) {
            proposal.setStatus(newStatus.name());
            return;
        }

        boolean transitionAllowed = switch (currentStatus) {
            case RECEIVED -> newStatus == ProposalStatus.IN_TRIAGE;
            case IN_TRIAGE -> newStatus == ProposalStatus.ELIGIBLE || newStatus == ProposalStatus.PENDING_ADJUSTMENTS;
            case PENDING_ADJUSTMENTS -> newStatus == ProposalStatus.IN_TRIAGE;
            case ELIGIBLE -> false;
        };

        if (!transitionAllowed) {
            throw new BadRequestException(
                    "Transição de status inválida na Etapa 1: " + currentStatus + " -> " + newStatus);
        }

        if (newStatus == ProposalStatus.ELIGIBLE) {
            validateTriageGate(proposal.getEnterprise());
        }

        proposal.setStatus(newStatus.name());
    }

    private ProposalStatus normalizeTriageStatus(String status) {
        if (status == null || status.isBlank()) {
            return ProposalStatus.RECEIVED;
        }

        return switch (status.trim().toUpperCase()) {
            case "RECEIVED" -> ProposalStatus.RECEIVED;
            case "IN_TRIAGE" -> ProposalStatus.IN_TRIAGE;
            case "ELIGIBLE" -> ProposalStatus.ELIGIBLE;
            case "PENDING_ADJUSTMENTS" -> ProposalStatus.PENDING_ADJUSTMENTS;
            default -> throw new BadRequestException("Status atual da proposta não é compatível com a Etapa 1: " + status);
        };
    }

    private void validateTriageGate(Enterprise enterprise) {
        List<String> pendingFields = new ArrayList<>();

        if (!hasText(enterprise.getLegalName())) pendingFields.add("razaoSocial");
        if (!hasText(enterprise.getCnpj())) pendingFields.add("cnpj");
        if (!hasText(enterprise.getEmail())) pendingFields.add("email");
        if (!hasText(enterprise.getPhone())) pendingFields.add("telefone");
        if (!hasText(enterprise.getRepresentativeName())) pendingFields.add("representanteNome");
        if (!hasText(enterprise.getRepresentativeCpf())) pendingFields.add("representanteCpf");
        if (!hasText(enterprise.getRepresentativePhone())) pendingFields.add("representanteContato");

        Address address = enterprise.getAddress();
        if (address == null) {
            pendingFields.add("endereco");
        } else {
            if (!hasText(address.getStreet())) pendingFields.add("endereco.logradouro");
            if (!hasText(address.getNumber())) pendingFields.add("endereco.numero");
            if (!hasText(address.getNeighborhood())) pendingFields.add("endereco.bairro");
            if (!hasText(address.getCity())) pendingFields.add("endereco.cidade");
            if (!hasText(address.getState())) pendingFields.add("endereco.uf");
            if (!hasText(address.getZipCode())) pendingFields.add("endereco.cep");
        }

        if (!pendingFields.isEmpty()) {
            throw new BadRequestException(
                    "Dados iniciais incompletos para avançar a proposta para ELIGIBLE: " + String.join(", ", pendingFields));
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
