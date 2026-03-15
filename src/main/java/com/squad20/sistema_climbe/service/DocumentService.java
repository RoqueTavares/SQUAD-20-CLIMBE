package com.squad20.sistema_climbe.service;

import com.squad20.sistema_climbe.entity.Document;
import com.squad20.sistema_climbe.entity.Enterprise;
import com.squad20.sistema_climbe.entity.User;
import com.squad20.sistema_climbe.entityDTO.DocumentDTO;
import com.squad20.sistema_climbe.exception.ResourceNotFoundException;
import com.squad20.sistema_climbe.repository.DocumentRepository;
import com.squad20.sistema_climbe.repository.EnterpriseRepository;
import com.squad20.sistema_climbe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<DocumentDTO> findAll() {
        return documentRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DocumentDTO> findByEnterpriseId(Long enterpriseId) {
        return documentRepository.findByEnterprise_Id(enterpriseId).stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DocumentDTO> findByAnalystId(Long analystId) {
        return documentRepository.findByAnalyst_Id(analystId).stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public DocumentDTO findById(Long id) {
        Document document = findDocumentOrThrow(id);
        return toDTO(document);
    }

    @Transactional
    public DocumentDTO save(DocumentDTO dto) {
        Enterprise enterprise = enterpriseRepository.findById(dto.getEnterpriseId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada com id: " + dto.getEnterpriseId()));
        User analyst = dto.getAnalystId() != null
                ? userRepository.findById(dto.getAnalystId())
                    .orElseThrow(() -> new ResourceNotFoundException("Analista não encontrado com id: " + dto.getAnalystId()))
                : null;
        Document document = toEntity(dto, enterprise, analyst);
        document = documentRepository.save(document);
        return toDTO(document);
    }

    @Transactional
    public DocumentDTO update(Long id, DocumentDTO dto) {
        Document existing = findDocumentOrThrow(id);
        if (dto.getDocumentType() != null) existing.setDocumentType(dto.getDocumentType());
        if (dto.getUrl() != null) existing.setUrl(dto.getUrl());
        if (dto.getValidated() != null) existing.setValidated(dto.getValidated());
        if (dto.getEnterpriseId() != null) {
            Enterprise enterprise = enterpriseRepository.findById(dto.getEnterpriseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada com id: " + dto.getEnterpriseId()));
            existing.setEnterprise(enterprise);
        }
        if (dto.getAnalystId() != null) {
            User analyst = userRepository.findById(dto.getAnalystId())
                    .orElseThrow(() -> new ResourceNotFoundException("Analista não encontrado com id: " + dto.getAnalystId()));
            existing.setAnalyst(analyst);
        } else if (existing.getAnalyst() != null) {
            existing.setAnalyst(null);
        }
        existing = documentRepository.save(existing);
        return toDTO(existing);
    }

    @Transactional
    public void delete(Long id) {
        Document document = findDocumentOrThrow(id);
        documentRepository.delete(document);
    }

    private Document findDocumentOrThrow(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Documento não encontrado com id: " + id));
    }

    private DocumentDTO toDTO(Document d) {
        Long enterpriseId = null;
        String enterpriseName = null;
        if (d.getEnterprise() != null) {
            enterpriseId = d.getEnterprise().getId();
            enterpriseName = d.getEnterprise().getTradeName() != null
                    ? d.getEnterprise().getTradeName() : d.getEnterprise().getLegalName();
        }
        return DocumentDTO.builder()
                .id(d.getId())
                .enterpriseId(enterpriseId)
                .enterpriseName(enterpriseName)
                .documentType(d.getDocumentType())
                .url(d.getUrl())
                .validated(d.getValidated())
                .analystId(d.getAnalyst() != null ? d.getAnalyst().getId() : null)
                .analystName(d.getAnalyst() != null ? d.getAnalyst().getFullName() : null)
                .build();
    }

    private Document toEntity(DocumentDTO dto, Enterprise enterprise, User analyst) {
        return Document.builder()
                .enterprise(enterprise)
                .documentType(dto.getDocumentType())
                .url(dto.getUrl())
                .validated(dto.getValidated())
                .analyst(analyst)
                .build();
    }
}
