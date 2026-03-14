package com.squad20.sistema_climbe.service;

import com.squad20.sistema_climbe.entity.OfferedService;
import com.squad20.sistema_climbe.entityDTO.ServiceDTO;
import com.squad20.sistema_climbe.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;

    public List<ServiceDTO> findAll() {
        return serviceRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public ServiceDTO findById(Long id) {
        OfferedService entity = findServiceOrThrow(id);
        return toDTO(entity);
    }

    public ServiceDTO save(ServiceDTO dto) {
        OfferedService entity = toEntity(dto);
        entity = serviceRepository.save(entity);
        return toDTO(entity);
    }

    public ServiceDTO update(Long id, ServiceDTO dto) {
        OfferedService entity = findServiceOrThrow(id);
        entity.setName(dto.getName());
        entity = serviceRepository.save(entity);
        return toDTO(entity);
    }

    public void delete(Long id) {
        OfferedService entity = findServiceOrThrow(id);
        serviceRepository.delete(entity);
    }

    private OfferedService findServiceOrThrow(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado com id: " + id));
    }

    private ServiceDTO toDTO(OfferedService entity) {
        return ServiceDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    private OfferedService toEntity(ServiceDTO dto) {
        OfferedService entity = new OfferedService();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        return entity;
    }
}

