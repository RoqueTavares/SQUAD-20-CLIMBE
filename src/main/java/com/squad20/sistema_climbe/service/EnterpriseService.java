package com.squad20.sistema_climbe.service;

import com.squad20.sistema_climbe.entity.Enterprise;
import com.squad20.sistema_climbe.entityDTO.EnterpriseDTO;
import com.squad20.sistema_climbe.exception.BadRequestException;
import com.squad20.sistema_climbe.exception.ConflictException;
import com.squad20.sistema_climbe.exception.ResourceNotFoundException;
import com.squad20.sistema_climbe.repository.EnterpriseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnterpriseService {

    private final EnterpriseRepository enterpriseRepository;

    @Transactional(readOnly = true)
    public List<EnterpriseDTO> findAll() {
        return enterpriseRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public EnterpriseDTO findById(Long id) {
        Enterprise enterprise = findEnterpriseOrThrow(id);
        return toDTO(enterprise);
    }

    @Transactional(readOnly = true)
    public EnterpriseDTO findByEmail(String email) {
        Enterprise enterprise = enterpriseRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));
        return toDTO(enterprise);
    }

    @Transactional(readOnly = true)
    public EnterpriseDTO findByCnpj(String cnpj) {
        Enterprise enterprise = enterpriseRepository.findByCnpj(cnpj)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));
        return toDTO(enterprise);
    }

    @Transactional
    public EnterpriseDTO save(EnterpriseDTO dto) {
        if (dto.getCnpj() == null || dto.getCnpj().isBlank()) {
            throw new BadRequestException("CNPJ da empresa é obrigatório");
        }
        if (enterpriseRepository.findByCnpj(dto.getCnpj()).isPresent()) {
            throw new ConflictException("Já existe empresa cadastrada com este CNPJ");
        }
        if (dto.getEmail() != null && !dto.getEmail().isBlank()
                && enterpriseRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ConflictException("Já existe empresa cadastrada com este e-mail");
        }
        Enterprise enterprise = toEntity(dto);
        enterprise = enterpriseRepository.save(enterprise);
        return toDTO(enterprise);
    }

    @Transactional
    public EnterpriseDTO update(Long id, EnterpriseDTO dto) {
        Enterprise existing = findEnterpriseOrThrow(id);
        if (dto.getCnpj() != null && !dto.getCnpj().equals(existing.getCnpj())
                && enterpriseRepository.findByCnpj(dto.getCnpj())
                .filter(e -> !e.getId().equals(id)).isPresent()) {
            throw new ConflictException("Já existe empresa cadastrada com este CNPJ");
        }
        if (dto.getEmail() != null && !dto.getEmail().isBlank() && !dto.getEmail().equals(existing.getEmail())
                && enterpriseRepository.findByEmail(dto.getEmail())
                .filter(e -> !e.getId().equals(id)).isPresent()) {
            throw new ConflictException("Já existe empresa cadastrada com este e-mail");
        }
        if (dto.getLegalName() != null) existing.setLegalName(dto.getLegalName());
        if (dto.getTradeName() != null) existing.setTradeName(dto.getTradeName());
        if (dto.getCnpj() != null) existing.setCnpj(dto.getCnpj());
        if (dto.getEmail() != null) existing.setEmail(dto.getEmail());
        if (dto.getStreet() != null) existing.setStreet(dto.getStreet());
        if (dto.getNeighborhood() != null) existing.setNeighborhood(dto.getNeighborhood());
        if (dto.getCity() != null) existing.setCity(dto.getCity());
        if (dto.getNumber() != null) existing.setNumber(dto.getNumber());
        if (dto.getState() != null) existing.setState(dto.getState());
        if (dto.getZipCode() != null) existing.setZipCode(dto.getZipCode());
        if (dto.getPhone() != null) existing.setPhone(dto.getPhone());
        if (dto.getRepresentativeName() != null) existing.setRepresentativeName(dto.getRepresentativeName());
        if (dto.getRepresentativeCpf() != null) existing.setRepresentativeCpf(dto.getRepresentativeCpf());
        if (dto.getRepresentativePhone() != null) existing.setRepresentativePhone(dto.getRepresentativePhone());
        existing = enterpriseRepository.save(existing);
        return toDTO(existing);
    }

    @Transactional
    public void delete(Long id) {
        Enterprise enterprise = findEnterpriseOrThrow(id);
        enterpriseRepository.delete(enterprise);
    }

    private Enterprise findEnterpriseOrThrow(Long id) {
        return enterpriseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));
    }

    private EnterpriseDTO toDTO(Enterprise e) {
        return EnterpriseDTO.builder()
                .id(e.getId())
                .legalName(e.getLegalName())
                .tradeName(e.getTradeName())
                .cnpj(e.getCnpj())
                .street(e.getStreet())
                .number(e.getNumber())
                .neighborhood(e.getNeighborhood())
                .city(e.getCity())
                .state(e.getState())
                .zipCode(e.getZipCode())
                .phone(e.getPhone())
                .email(e.getEmail())
                .representativeName(e.getRepresentativeName())
                .representativeCpf(e.getRepresentativeCpf())
                .representativePhone(e.getRepresentativePhone())
                .build();
    }

    private Enterprise toEntity(EnterpriseDTO dto) {
        return Enterprise.builder()
                .legalName(dto.getLegalName())
                .tradeName(dto.getTradeName())
                .cnpj(dto.getCnpj())
                .street(dto.getStreet())
                .number(dto.getNumber())
                .neighborhood(dto.getNeighborhood())
                .city(dto.getCity())
                .state(dto.getState())
                .zipCode(dto.getZipCode())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .representativeName(dto.getRepresentativeName())
                .representativeCpf(dto.getRepresentativeCpf())
                .representativePhone(dto.getRepresentativePhone())
                .build();
    }
}
