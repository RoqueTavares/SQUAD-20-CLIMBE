package com.squad20.sistema_climbe.mapper;

import com.squad20.sistema_climbe.dto.ContractDTO;
import com.squad20.sistema_climbe.entity.Contract;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ContractMapper {

    @Mapping(source = "proposal.id", target = "proposalId")
    ContractDTO toDTO(Contract contract);

    @Mapping(target = "proposal", ignore = true)
    Contract toEntity(ContractDTO dto);
}
