package com.squad20.sistema_climbe.mapper;

import com.squad20.sistema_climbe.dto.PermissionDTO;
import com.squad20.sistema_climbe.entity.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PermissionMapper {

    PermissionDTO toDTO(Permission permission);

    Permission toEntity(PermissionDTO dto);
}
