package com.squad20.sistema_climbe.service;

import com.squad20.sistema_climbe.entity.Contract;
import com.squad20.sistema_climbe.entity.Proposal;
import com.squad20.sistema_climbe.entityDTO.ContractDTO;
import com.squad20.sistema_climbe.exception.ResourceNotFoundException;
import com.squad20.sistema_climbe.repository.ContractRepository;
import com.squad20.sistema_climbe.repository.ProposalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;
    private final ProposalRepository proposalRepository;

    @Transactional(readOnly = true)
    public List<ContractDTO> findAll() {
        return contractRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ContractDTO> findByProposalId(Long proposalId) {
        return contractRepository.findByProposal_Id(proposalId).stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ContractDTO findById(Long id) {
        Contract contract = findContractOrThrow(id);
        return toDTO(contract);
    }

    @Transactional
    public ContractDTO save(ContractDTO dto) {
        Proposal proposal = proposalRepository.findById(dto.getProposalId())
                .orElseThrow(() -> new ResourceNotFoundException("Proposta não encontrada com id: " + dto.getProposalId()));
        Contract contract = toEntity(dto, proposal);
        contract = contractRepository.save(contract);
        return toDTO(contract);
    }

    @Transactional
    public ContractDTO update(Long id, ContractDTO dto) {
        Contract existing = findContractOrThrow(id);
        if (dto.getStartDate() != null) existing.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) existing.setEndDate(dto.getEndDate());
        if (dto.getStatus() != null) existing.setStatus(dto.getStatus());
        if (dto.getProposalId() != null) {
            Proposal proposal = proposalRepository.findById(dto.getProposalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Proposta não encontrada com id: " + dto.getProposalId()));
            existing.setProposal(proposal);
        }
        existing = contractRepository.save(existing);
        return toDTO(existing);
    }

    @Transactional
    public void delete(Long id) {
        Contract contract = findContractOrThrow(id);
        contractRepository.delete(contract);
    }

    private Contract findContractOrThrow(Long id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrato não encontrado com id: " + id));
    }

    private ContractDTO toDTO(Contract c) {
        return ContractDTO.builder()
                .id(c.getId())
                .proposalId(c.getProposal() != null ? c.getProposal().getId() : null)
                .startDate(c.getStartDate())
                .endDate(c.getEndDate())
                .status(c.getStatus())
                .build();
    }

    private Contract toEntity(ContractDTO dto, Proposal proposal) {
        return Contract.builder()
                .proposal(proposal)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .status(dto.getStatus())
                .build();
    }
}
