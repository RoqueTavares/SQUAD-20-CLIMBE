package com.squad20.sistema_climbe.service;

import com.squad20.sistema_climbe.entity.Enterprise;
import com.squad20.sistema_climbe.entity.User;
import com.squad20.sistema_climbe.entityDTO.EnterpriseDTO;
import com.squad20.sistema_climbe.entityDTO.UserDTO;
import com.squad20.sistema_climbe.repository.EnterpriseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EnterpriseService {

    private final EnterpriseRepository enterpriseRepository;

    public List<EnterpriseDTO> findAll() {
        List<EnterpriseDTO> enterprise= enterpriseRepository.findAll().stream()
                .map(user -> new EnterpriseDTO(user))
                .toList();

        return enterprise ;
    }

    public EnterpriseDTO findById(int id) {
        Optional<Enterprise> enterprise = enterpriseRepository.findById(id);

        if (enterprise.isPresent()) {
            return new EnterpriseDTO(enterprise.get());
        }else{
            throw new RuntimeException("Enterprise not found");
        }
    }

    public EnterpriseDTO findByEmail(String email) {
        Optional<Enterprise> enterprise = enterpriseRepository.findByEmail(email);

        if (enterprise.isPresent()) {
            return new EnterpriseDTO(enterprise.get());
        }else{
            throw new RuntimeException("Enterprise not found");
        }
    }

    public EnterpriseDTO findByCnpj(String cnpj) {
        Optional<Enterprise> enterprise = enterpriseRepository.findByCnpj(cnpj);

        if(enterprise.isPresent()) {
            return new EnterpriseDTO(enterprise.get());
        }else{
            throw new RuntimeException("Enterprise not found");
        }
    }

    @Transactional
    public Enterprise save(Enterprise enterprise) {

        if(enterprise.getCnpj() == null) {
            throw new RuntimeException("Enterprise cnpj is null");
        }

        if(enterpriseRepository.findByEmail(enterprise.getEmail()).isPresent()) {
            throw new RuntimeException("Enterprise already exists");
        }

       return enterpriseRepository.save(enterprise);

    }

    @Transactional
    public Enterprise update(int id, Enterprise enterprise) {
        Enterprise oldEnterprise = enterpriseRepository.getReferenceById(id);

        if(oldEnterprise != null) {
            oldEnterprise.setRazao_social(enterprise.getRazao_social());
            oldEnterprise.setCnpj(enterprise.getCnpj());
            oldEnterprise.setEmail(enterprise.getEmail());
            oldEnterprise.setLogradouro(enterprise.getLogradouro());
            oldEnterprise.setBairro(enterprise.getBairro());
            oldEnterprise.setCidade(enterprise.getCidade());
            oldEnterprise.setNumero(enterprise.getNumero());
            oldEnterprise.setUf(enterprise.getUf());
            oldEnterprise.setCep(enterprise.getCep());
            oldEnterprise.setTelefone(enterprise.getTelefone());
            oldEnterprise.setRepresentante_nome(enterprise.getRepresentante_nome());
            oldEnterprise.setRepresentante_cnpj(enterprise.getRepresentante_cnpj());
            oldEnterprise.setRepresentante_contato(enterprise.getRepresentante_contato());
        }else{
            throw new RuntimeException("Enterprise not found");
        }

        return enterpriseRepository.save(oldEnterprise);
    }
}
