package com.squad20.sistema_climbe.domain.cargo.mapper;

import com.squad20.sistema_climbe.domain.cargo.dto.CargoDTO;
import com.squad20.sistema_climbe.domain.cargo.entity.Cargo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CargoMapper {

    CargoDTO toDTO(Cargo cargo);

    Cargo toEntity(CargoDTO dto);
}
