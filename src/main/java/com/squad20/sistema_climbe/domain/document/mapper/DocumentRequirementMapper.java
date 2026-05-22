package com.squad20.sistema_climbe.domain.document.mapper;

import com.squad20.sistema_climbe.domain.document.dto.DocumentRequirementDTO;
import com.squad20.sistema_climbe.domain.document.entity.DocumentRequirement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DocumentRequirementMapper {

    @Mapping(source = "proposal.id", target = "proposalId")
    @Mapping(source = "document.id", target = "documentId")
    @Mapping(source = "validatedBy.id", target = "validatedById")
    DocumentRequirementDTO toDTO(DocumentRequirement entity);
}
