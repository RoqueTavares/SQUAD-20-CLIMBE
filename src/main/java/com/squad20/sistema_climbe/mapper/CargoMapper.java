package com.squad20.sistema_climbe.mapper;

import com.squad20.sistema_climbe.dto.CargoDTO;
import com.squad20.sistema_climbe.entity.Cargo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CargoMapper {

    CargoDTO toDTO(Cargo cargo);

    Cargo toEntity(CargoDTO dto);
}
