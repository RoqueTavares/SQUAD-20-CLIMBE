package com.squad20.sistema_climbe.domain.document.mapper;

import com.squad20.sistema_climbe.domain.document.dto.DocumentRequirementCreateRequest;
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

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "proposal", ignore = true)
    @Mapping(target = "document", ignore = true)
    @Mapping(target = "validatedBy", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "documentType", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    @Mapping(target = "validatedAt", ignore = true)
    DocumentRequirement toEntity(DocumentRequirementCreateRequest request);
}
