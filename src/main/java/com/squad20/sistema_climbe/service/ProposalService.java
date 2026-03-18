package com.squad20.sistema_climbe.service;

import com.squad20.sistema_climbe.entity.Enterprise;
import com.squad20.sistema_climbe.entity.Proposal;
import com.squad20.sistema_climbe.entity.User;
import com.squad20.sistema_climbe.dto.ProposalDTO;
import com.squad20.sistema_climbe.exception.ResourceNotFoundException;
import com.squad20.sistema_climbe.mapper.ProposalMapper;
import com.squad20.sistema_climbe.repository.EnterpriseRepository;
import com.squad20.sistema_climbe.repository.ProposalRepository;
import com.squad20.sistema_climbe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProposalService {

    private final ProposalRepository proposalRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final UserRepository userRepository;
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
    public ProposalDTO save(ProposalDTO dto) {
        Enterprise enterprise = enterpriseRepository.findById(dto.getEnterpriseId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada com id: " + dto.getEnterpriseId()));
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + dto.getUserId()));
        Proposal proposal = proposalMapper.toEntity(dto);
        proposal.setEnterprise(enterprise);
        proposal.setUser(user);
        if (proposal.getCreatedAt() == null) {
            proposal.setCreatedAt(LocalDateTime.now());
        }
        proposal = proposalRepository.save(proposal);
        return proposalMapper.toDTO(proposal);
    }

    @Transactional
    public ProposalDTO update(Long id, ProposalDTO dto) {
        Proposal existing = findProposalOrThrow(id);
        if (dto.getStatus() != null) existing.setStatus(dto.getStatus());
        if (dto.getEnterpriseId() != null) {
            Enterprise enterprise = enterpriseRepository.findById(dto.getEnterpriseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada com id: " + dto.getEnterpriseId()));
            existing.setEnterprise(enterprise);
        }
        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + dto.getUserId()));
            existing.setUser(user);
        }
        existing = proposalRepository.save(existing);
        return proposalMapper.toDTO(existing);
    }

    @Transactional
    public void delete(Long id) {
        Proposal proposal = findProposalOrThrow(id);
        proposalRepository.delete(proposal);
    }

    private Proposal findProposalOrThrow(Long id) {
        return proposalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proposta não encontrada com id: " + id));
    }
}
