package com.squad20.sistema_climbe.domain.contract.service;

import com.squad20.sistema_climbe.domain.contract.dto.ContractCreateRequest;
import com.squad20.sistema_climbe.domain.contract.dto.ContractDTO;
import com.squad20.sistema_climbe.domain.contract.dto.ContractPatchRequest;
import com.squad20.sistema_climbe.domain.contract.entity.Contract;
import com.squad20.sistema_climbe.domain.contract.mapper.ContractMapper;
import com.squad20.sistema_climbe.domain.contract.repository.ContractRepository;
import com.squad20.sistema_climbe.domain.proposal.entity.Proposal;
import com.squad20.sistema_climbe.domain.proposal.repository.ProposalRepository;
import com.squad20.sistema_climbe.domain.report.repository.ReportRepository;
import com.squad20.sistema_climbe.domain.spreadsheet.repository.SpreadsheetRepository;
import com.squad20.sistema_climbe.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;
    private final ProposalRepository proposalRepository;
    private final ReportRepository reportRepository;
    private final SpreadsheetRepository spreadsheetRepository;
    private final ContractMapper contractMapper;

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

    @Transactional
    public ContractDTO save(ContractCreateRequest request) {
        Proposal proposal = proposalRepository.findById(request.getProposalId())
                .orElseThrow(() -> new ResourceNotFoundException("Proposta não encontrada com id: " + request.getProposalId()));

        Contract contract = contractMapper.toEntity(request);
        contract.setId(null);
        contract.setProposal(proposal);
        contract = contractRepository.save(contract);
        return contractMapper.toDTO(contract);
    }

    @Transactional
    public ContractDTO update(Long id, ContractPatchRequest patch) {
        Contract existing = findContractOrThrow(id);

        if (patch.getStartDate() != null) existing.setStartDate(patch.getStartDate());
        if (patch.getEndDate() != null) existing.setEndDate(patch.getEndDate());
        if (patch.getStatus() != null) existing.setStatus(patch.getStatus());

        if (patch.getProposalId() != null) {
            Proposal proposal = proposalRepository.findById(patch.getProposalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Proposta não encontrada com id: " + patch.getProposalId()));
            existing.setProposal(proposal);
        }

        existing = contractRepository.save(existing);
        return contractMapper.toDTO(existing);
    }

    @Transactional
    public void delete(Long id) {
        Contract contract = findContractOrThrow(id);
        // Cascade soft delete: deletar contrato propaga para relatórios e planilhas.
        // Ordem importa: filhos antes do pai para evitar inconsistências.
        LocalDateTime now = LocalDateTime.now();
        reportRepository.softDeleteByContractId(id, now);
        spreadsheetRepository.softDeleteByContractId(id, now);

        contract.setDeletedAt(now);
        contractRepository.save(contract);
    }

    private Contract findContractOrThrow(Long id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrato não encontrado com id: " + id));
    }
}
