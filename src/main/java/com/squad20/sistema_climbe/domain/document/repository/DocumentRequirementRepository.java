package com.squad20.sistema_climbe.domain.document.repository;

import com.squad20.sistema_climbe.domain.document.entity.DocumentRequirement;
import com.squad20.sistema_climbe.domain.document.entity.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentRequirementRepository extends JpaRepository<DocumentRequirement, Long> {

    List<DocumentRequirement> findByProposal_Id(Long proposalId);

    Optional<DocumentRequirement> findByProposal_IdAndDocumentType(Long proposalId, DocumentType documentType);

    boolean existsByProposal_IdAndDocumentType(Long proposalId, DocumentType documentType);
}
