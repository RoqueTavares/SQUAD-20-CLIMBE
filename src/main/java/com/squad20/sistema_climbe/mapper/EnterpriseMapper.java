package com.squad20.sistema_climbe.mapper;

import com.squad20.sistema_climbe.dto.EnterpriseDTO;
import com.squad20.sistema_climbe.entity.Enterprise;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = AddressMapper.class)
public interface EnterpriseMapper {

    EnterpriseDTO toDTO(Enterprise enterprise);

    @Mapping(target = "services", ignore = true)
    Enterprise toEntity(EnterpriseDTO dto);
}
