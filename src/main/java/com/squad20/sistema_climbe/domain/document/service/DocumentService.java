package com.squad20.sistema_climbe.domain.document.service;

import com.squad20.sistema_climbe.domain.document.dto.DocumentCreateRequest;
import com.squad20.sistema_climbe.domain.document.dto.DocumentDTO;
import com.squad20.sistema_climbe.domain.document.dto.DocumentPatchRequest;
import com.squad20.sistema_climbe.domain.document.entity.Document;
import com.squad20.sistema_climbe.domain.document.mapper.DocumentMapper;
import com.squad20.sistema_climbe.domain.document.repository.DocumentRepository;
import com.squad20.sistema_climbe.domain.enterprise.entity.Enterprise;
import com.squad20.sistema_climbe.domain.enterprise.repository.EnterpriseRepository;
import com.squad20.sistema_climbe.domain.user.entity.User;
import com.squad20.sistema_climbe.domain.user.repository.UserRepository;
import com.squad20.sistema_climbe.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final UserRepository userRepository;
    private final DocumentMapper documentMapper;

    @Transactional(readOnly = true)
    public Page<DocumentDTO> findAll(Pageable pageable) {
        return documentRepository.findAll(pageable).map(documentMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public List<DocumentDTO> findByEnterpriseId(Long enterpriseId) {
        return documentRepository.findByEnterprise_Id(enterpriseId).stream()
                .map(documentMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DocumentDTO> findByAnalystId(Long analystId) {
        return documentRepository.findByAnalyst_Id(analystId).stream()
                .map(documentMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public DocumentDTO findById(Long id) {
        Document document = findDocumentOrThrow(id);
        return documentMapper.toDTO(document);
    }

    @Transactional
    public DocumentDTO save(DocumentCreateRequest request) {
        Enterprise enterprise = enterpriseRepository.findById(request.getEnterpriseId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada com id: " + request.getEnterpriseId()));
        User analyst = request.getAnalystId() != null
                ? userRepository.findById(request.getAnalystId())
                    .orElseThrow(() -> new ResourceNotFoundException("Analista não encontrado com id: " + request.getAnalystId()))
                : null;

        Document document = documentMapper.toEntity(request);
        document.setId(null);
        document.setEnterprise(enterprise);
        document.setAnalyst(analyst);
        document = documentRepository.save(document);
        return documentMapper.toDTO(document);
    }

    @Transactional
    public DocumentDTO update(Long id, DocumentPatchRequest patch) {
        Document existing = findDocumentOrThrow(id);

        if (patch.getDocumentType() != null) existing.setDocumentType(patch.getDocumentType());
        if (patch.getUrl() != null) existing.setUrl(patch.getUrl());
        if (patch.getValidated() != null) existing.setValidated(patch.getValidated());

        if (patch.getEnterpriseId() != null) {
            Enterprise enterprise = enterpriseRepository.findById(patch.getEnterpriseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada com id: " + patch.getEnterpriseId()));
            existing.setEnterprise(enterprise);
        }

        if (patch.getAnalystId() != null) {
            User analyst = userRepository.findById(patch.getAnalystId())
                    .orElseThrow(() -> new ResourceNotFoundException("Analista não encontrado com id: " + patch.getAnalystId()));
            existing.setAnalyst(analyst);
        } else if (existing.getAnalyst() != null) {
            existing.setAnalyst(null);
        }

        existing = documentRepository.save(existing);
        return documentMapper.toDTO(existing);
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
}

