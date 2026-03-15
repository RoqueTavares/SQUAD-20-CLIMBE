package com.squad20.sistema_climbe.service;

import com.squad20.sistema_climbe.entity.OfferedService;
import com.squad20.sistema_climbe.entityDTO.ServiceDTO;
import com.squad20.sistema_climbe.exception.ResourceNotFoundException;
import com.squad20.sistema_climbe.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;

    @Transactional(readOnly = true)
    public List<ServiceDTO> findAll() {
        return serviceRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ServiceDTO findById(Long id) {
        OfferedService entity = findServiceOrThrow(id);
        return toDTO(entity);
    }

    @Transactional
    public ServiceDTO save(ServiceDTO dto) {
        OfferedService entity = toEntity(dto);
        entity = serviceRepository.save(entity);
        return toDTO(entity);
    }

    @Transactional
    public ServiceDTO update(Long id, ServiceDTO dto) {
        OfferedService entity = findServiceOrThrow(id);
        if (dto.getName() != null) entity.setName(dto.getName());
        entity = serviceRepository.save(entity);
        return toDTO(entity);
    }

    @Transactional
    public void delete(Long id) {
        OfferedService entity = findServiceOrThrow(id);
        serviceRepository.delete(entity);
    }

    private OfferedService findServiceOrThrow(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado com id: " + id));
    }

    private ServiceDTO toDTO(OfferedService entity) {
        return ServiceDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    private OfferedService toEntity(ServiceDTO dto) {
        OfferedService entity = new OfferedService();
        entity.setName(dto.getName());
        return entity;
    }
}

