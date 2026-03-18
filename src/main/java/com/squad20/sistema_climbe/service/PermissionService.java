package com.squad20.sistema_climbe.service;

import com.squad20.sistema_climbe.entity.Permission;
import com.squad20.sistema_climbe.dto.PermissionDTO;
import com.squad20.sistema_climbe.exception.ResourceNotFoundException;
import com.squad20.sistema_climbe.mapper.PermissionMapper;
import com.squad20.sistema_climbe.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    @Transactional(readOnly = true)
    public Page<PermissionDTO> findAll(Pageable pageable) {
        return permissionRepository.findAll(pageable).map(permissionMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public PermissionDTO findById(Long id) {
        Permission permission = findPermissionOrThrow(id);
        return permissionMapper.toDTO(permission);
    }

    @Transactional
    public PermissionDTO save(PermissionDTO dto) {
        Permission permission = permissionMapper.toEntity(dto);
        permission = permissionRepository.save(permission);
        return permissionMapper.toDTO(permission);
    }

    @Transactional
    public PermissionDTO update(Long id, PermissionDTO dto) {
        Permission permission = findPermissionOrThrow(id);
        if (dto.getDescription() != null) permission.setDescription(dto.getDescription());
        permission = permissionRepository.save(permission);
        return permissionMapper.toDTO(permission);
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
}

