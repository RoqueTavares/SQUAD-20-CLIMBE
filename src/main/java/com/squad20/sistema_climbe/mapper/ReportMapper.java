package com.squad20.sistema_climbe.mapper;

import com.squad20.sistema_climbe.dto.ReportDTO;
import com.squad20.sistema_climbe.entity.Report;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReportMapper {

    @Mapping(source = "contract.id", target = "contractId")
    ReportDTO toDTO(Report report);

    @Mapping(target = "contract", ignore = true)
    Report toEntity(ReportDTO dto);
}
