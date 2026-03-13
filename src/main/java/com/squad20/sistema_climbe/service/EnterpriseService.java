package com.squad20.sistema_climbe.service;

import com.squad20.sistema_climbe.entity.Enterprise;
import com.squad20.sistema_climbe.dto.EnterpriseDTO;
import com.squad20.sistema_climbe.repository.EnterpriseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EnterpriseService {

    private final EnterpriseRepository enterpriseRepository;

    public List<EnterpriseDTO> findAll() {
        return enterpriseRepository.findAll().stream()
                .map(EnterpriseDTO::new)
                .toList();
    }

    public EnterpriseDTO findById(int id) {
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
    public Enterprise save(Enterprise enterprise) {
        if (enterprise.getCnpj() == null) {
            throw new RuntimeException("CNPJ da empresa é obrigatório");
        }
        if (enterpriseRepository.findByEmail(enterprise.getEmail()).isPresent()) {
            throw new RuntimeException("Já existe empresa cadastrada com este e-mail");
        }
        return enterpriseRepository.save(enterprise);
    }

    @Transactional
    public Enterprise update(int id, Enterprise enterprise) {
        Enterprise existing = enterpriseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
        existing.setLegalName(enterprise.getLegalName());
        existing.setTradeName(enterprise.getTradeName());
        existing.setCnpj(enterprise.getCnpj());
        existing.setEmail(enterprise.getEmail());
        existing.setStreet(enterprise.getStreet());
        existing.setNeighborhood(enterprise.getNeighborhood());
        existing.setCity(enterprise.getCity());
        existing.setNumber(enterprise.getNumber());
        existing.setState(enterprise.getState());
        existing.setZipCode(enterprise.getZipCode());
        existing.setPhone(enterprise.getPhone());
        existing.setRepresentativeName(enterprise.getRepresentativeName());
        existing.setRepresentativeCpf(enterprise.getRepresentativeCpf());
        existing.setRepresentativePhone(enterprise.getRepresentativePhone());
        return enterpriseRepository.save(existing);
    }
}
