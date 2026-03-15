package com.squad20.sistema_climbe.service;

import com.squad20.sistema_climbe.entity.Permission;
import com.squad20.sistema_climbe.entityDTO.PermissionDTO;
import com.squad20.sistema_climbe.exception.ResourceNotFoundException;
import com.squad20.sistema_climbe.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    public List<PermissionDTO> findAll() {
        return permissionRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public PermissionDTO findById(Long id) {
        Permission permission = findPermissionOrThrow(id);
        return toDTO(permission);
    }

    @Transactional
    public PermissionDTO save(PermissionDTO dto) {
        Permission permission = toEntity(dto);
        permission = permissionRepository.save(permission);
        return toDTO(permission);
    }

    @Transactional
    public PermissionDTO update(Long id, PermissionDTO dto) {
        Permission permission = findPermissionOrThrow(id);
        if (dto.getDescription() != null) permission.setDescription(dto.getDescription());
        permission = permissionRepository.save(permission);
        return toDTO(permission);
    }

    @Transactional
    public void delete(Long id) {
        Permission permission = findPermissionOrThrow(id);
        permissionRepository.delete(permission);
    }

    private Permission findPermissionOrThrow(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permissão não encontrada com id: " + id));
    }

    private PermissionDTO toDTO(Permission permission) {
        return PermissionDTO.builder()
                .id(permission.getId())
                .description(permission.getDescription())
                .build();
    }

    private Permission toEntity(PermissionDTO dto) {
        return Permission.builder()
                .description(dto.getDescription())
                .build();
    }
}

