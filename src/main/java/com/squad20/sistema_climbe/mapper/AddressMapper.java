package com.squad20.sistema_climbe.mapper;

import com.squad20.sistema_climbe.dto.AddressDTO;
import com.squad20.sistema_climbe.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AddressMapper {

    AddressDTO toDTO(Address address);

    Address toEntity(AddressDTO dto);
}
