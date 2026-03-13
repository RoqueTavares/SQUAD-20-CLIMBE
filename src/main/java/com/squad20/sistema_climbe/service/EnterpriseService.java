package com.squad20.sistema_climbe.service;

import com.squad20.sistema_climbe.entity.Enterprise;
import com.squad20.sistema_climbe.entityDTO.EnterpriseDTO;
import com.squad20.sistema_climbe.repository.EnterpriseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnterpriseService {

    private final EnterpriseRepository enterpriseRepository;

    public List<EnterpriseDTO> findAll() {
        return enterpriseRepository.findAll().stream()
                .map(EnterpriseDTO::new)
                .toList();
    }

    public EnterpriseDTO findById(Long id) {
        Enterprise enterprise = enterpriseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
        return new EnterpriseDTO(enterprise);
    }

    public EnterpriseDTO findByEmail(String email) {
        Enterprise enterprise = enterpriseRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
        return new EnterpriseDTO(enterprise);
    }

    public EnterpriseDTO findByCnpj(String cnpj) {
        Enterprise enterprise = enterpriseRepository.findByCnpj(cnpj)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
        return new EnterpriseDTO(enterprise);
    }

    @Transactional
    public EnterpriseDTO save(EnterpriseDTO dto) {
        if (dto.getCnpj() == null || dto.getCnpj().isBlank()) {
            throw new RuntimeException("CNPJ da empresa é obrigatório");
        }
        if (enterpriseRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Já existe empresa cadastrada com este e-mail");
        }
        Enterprise enterprise = toEntity(dto);
        enterprise = enterpriseRepository.save(enterprise);
        return new EnterpriseDTO(enterprise);
    }

    @Transactional
    public EnterpriseDTO update(Long id, EnterpriseDTO dto) {
        Enterprise existing = enterpriseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
        existing.setLegalName(dto.getLegalName());
        existing.setTradeName(dto.getTradeName());
        existing.setCnpj(dto.getCnpj());
        existing.setEmail(dto.getEmail());
        existing.setStreet(dto.getStreet());
        existing.setNeighborhood(dto.getNeighborhood());
        existing.setCity(dto.getCity());
        existing.setNumber(dto.getNumber());
        existing.setState(dto.getState());
        existing.setZipCode(dto.getZipCode());
        existing.setPhone(dto.getPhone());
        existing.setRepresentativeName(dto.getRepresentativeName());
        existing.setRepresentativeCpf(dto.getRepresentativeCpf());
        existing.setRepresentativePhone(dto.getRepresentativePhone());
        existing = enterpriseRepository.save(existing);
        return new EnterpriseDTO(existing);
    }

    private Enterprise toEntity(EnterpriseDTO dto) {
        Enterprise e = new Enterprise();
        e.setLegalName(dto.getLegalName());
        e.setTradeName(dto.getTradeName());
        e.setCnpj(dto.getCnpj());
        e.setStreet(dto.getStreet());
        e.setNumber(dto.getNumber());
        e.setNeighborhood(dto.getNeighborhood());
        e.setCity(dto.getCity());
        e.setState(dto.getState());
        e.setZipCode(dto.getZipCode());
        e.setPhone(dto.getPhone());
        e.setEmail(dto.getEmail());
        e.setRepresentativeName(dto.getRepresentativeName());
        e.setRepresentativeCpf(dto.getRepresentativeCpf());
        e.setRepresentativePhone(dto.getRepresentativePhone());
        return e;
    }
}
