package com.squad20.sistema_climbe.mapper;

import com.squad20.sistema_climbe.dto.ServiceDTO;
import com.squad20.sistema_climbe.entity.OfferedService;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OfferedServiceMapper {

    ServiceDTO toDTO(OfferedService entity);

    OfferedService toEntity(ServiceDTO dto);
}
