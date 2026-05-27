package com.squad20.sistema_climbe.domain.report.service;

import com.squad20.sistema_climbe.domain.report.dto.ReportCreateRequest;
import com.squad20.sistema_climbe.domain.report.dto.ReportDTO;
import com.squad20.sistema_climbe.domain.report.dto.ReportPatchRequest;
import com.squad20.sistema_climbe.domain.report.entity.Report;
import com.squad20.sistema_climbe.domain.report.mapper.ReportMapper;
import com.squad20.sistema_climbe.domain.report.repository.ReportRepository;
import com.squad20.sistema_climbe.domain.contract.entity.Contract;
import com.squad20.sistema_climbe.exception.ResourceNotFoundException;
import com.squad20.sistema_climbe.exception.BadRequestException;
import com.squad20.sistema_climbe.service.GoogleCloudStorageService;
import com.google.cloud.storage.StorageException;
import com.squad20.sistema_climbe.domain.contract.repository.ContractRepository;
import com.squad20.sistema_climbe.domain.user.entity.User;
import com.squad20.sistema_climbe.domain.user.repository.UserRepository;
import com.squad20.sistema_climbe.domain.report.entity.ReportStatus;
import com.squad20.sistema_climbe.domain.notification.service.NotificationService;
import com.squad20.sistema_climbe.domain.notification.dto.NotificationCreateRequest;
import com.squad20.sistema_climbe.domain.service.entity.OfferedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Locale;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ContractRepository contractRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final ReportMapper reportMapper;
    private final GoogleCloudStorageService storageService;

    @Transactional(readOnly = true)
    public Page<ReportDTO> findAll(Pageable pageable) {
        return reportRepository.findAll(pageable).map(reportMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public List<ReportDTO> findByContractId(Long contractId) {
        return reportRepository.findByContract_Id(contractId).stream()
                .map(reportMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReportDTO findById(Long id) {
        Report report = findReportOrThrow(id);
        return reportMapper.toDTO(report);
    }

    @Transactional
    public ReportDTO save(ReportCreateRequest request) {
        Contract contract = contractRepository.findById(request.getContractId())
                .orElseThrow(() -> new ResourceNotFoundException("Contrato não encontrado com id: " + request.getContractId()));

        Report report = reportMapper.toEntity(request);
        report.setId(null);
        report.setContract(contract);
        report.setStatus(ReportStatus.DRAFT);
        report = reportRepository.save(report);
        return reportMapper.toDTO(report);
    }

    @Transactional
    public ReportDTO saveWithFile(ReportCreateRequest request, MultipartFile file) throws IOException {
        validatePdfFile(file);

        try {
            String internalPath = storageService.uploadPrivateFile(file, "relatorios_contrato_" + request.getContractId());
            request.setPdfUrl(internalPath);
            if (request.getSentAt() == null) {
                request.setSentAt(LocalDateTime.now());
            }
        } catch (StorageException e) {
            log.error("Falha ao enviar relatorio para o Google Cloud Storage: {}", e.getMessage(), e);
            throw new BadRequestException("Falha ao enviar PDF para o armazenamento. Verifique a configuracao do bucket.");
        }

        return save(request);
    }

    @Transactional(readOnly = true)
    public String generateViewUrl(Long id) {
        Report report = findReportOrThrow(id);
        if (report.getPdfUrl() == null || report.getPdfUrl().isBlank()) {
            throw new ResourceNotFoundException("Este relatorio nao possui PDF anexado.");
        }

        if (report.getPdfUrl().startsWith("http")) {
            return report.getPdfUrl();
        }

        return storageService.generateSignedUrl(report.getPdfUrl());
    }

    @Transactional
    public ReportDTO update(Long id, ReportPatchRequest patch) {
        Report existing = findReportOrThrow(id);

        if (patch.getPdfUrl() != null) existing.setPdfUrl(patch.getPdfUrl());
        if (patch.getSentAt() != null) existing.setSentAt(patch.getSentAt());

        if (patch.getContractId() != null) {
            Contract contract = contractRepository.findById(patch.getContractId())
                    .orElseThrow(() -> new ResourceNotFoundException("Contrato não encontrado com id: " + patch.getContractId()));
            existing.setContract(contract);
        }

        existing = reportRepository.save(existing);
        return reportMapper.toDTO(existing);
    }

    @Transactional
    public void delete(Long id) {
        Report report = findReportOrThrow(id);
        report.setDeletedAt(java.time.LocalDateTime.now());
        reportRepository.save(report);
    }

    @Transactional
    public ReportDTO submitForReview(Long id) {
        Report report = findReportOrThrow(id);
        report.setStatus(ReportStatus.IN_REVIEW);
        report = reportRepository.save(report);

        // Notify Senior Analyst
        List<User> seniors = userRepository.findByRole(com.squad20.sistema_climbe.domain.user.entity.Role.ANALISTA_SENIOR);
        for (User senior : seniors) {
            notificationService.save(NotificationCreateRequest.builder()
                    .userId(senior.getId())
                    .type("REPORT_REVIEW_REQUIRED")
                    .message("Relatório " + report.getId() + " do contrato " + report.getContract().getId() + " aguarda revisão.")
                    .build());
        }

        return reportMapper.toDTO(report);
    }

    @Transactional
    public ReportDTO reviewReport(Long id, Long reviewerId, boolean approved) {
        Report report = findReportOrThrow(id);
        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new ResourceNotFoundException("Revisor não encontrado."));
        
        report.setReviewer(reviewer);
        if (approved) {
            report.setStatus(ReportStatus.APPROVED);
            schedulePresentation(report.getContract());
        } else {
            report.setStatus(ReportStatus.REJECTED);
        }
        report = reportRepository.save(report);
        return reportMapper.toDTO(report);
    }

    private void schedulePresentation(Contract contract) {
        // MOCK: Integration with MeetingService
        System.out.println("MOCK: Reunião de Apresentação agendada para o contrato " + contract.getId());
    }

    @Transactional
    public void completePresentation(Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contrato não encontrado."));
        
        System.out.println("MOCK: Reunião de Apresentação concluída para o contrato " + contract.getId());
        
        boolean hasRecurrence = false;
        if (contract.getProposal() != null && contract.getProposal().getEnterprise() != null) {
            java.util.Set<OfferedService> services = contract.getProposal().getEnterprise().getServices();
            if (services != null) {
                for (OfferedService srv : services) {
                    if (srv.getName().toUpperCase().contains("CFO") || srv.getName().toUpperCase().contains("BPO_FINANCEIRO")) {
                        hasRecurrence = true;
                        break;
                    }
                }
            }
        }
        
        if (hasRecurrence) {
            System.out.println("MOCK: Gateway de Recorrência (CFO/FS) acionado! Regerando DocumentRequirements para o próximo ciclo...");
            // TODO: call DocumentRequirementService.createRequirements() for next month.
        } else {
            System.out.println("MOCK: Fim do processo. Sem recorrência para o contrato " + contract.getId());
        }
    }

    private Report findReportOrThrow(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Relatório não encontrado com id: " + id));
    }

    private void validatePdfFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Selecione um arquivo PDF para o relatorio.");
        }

        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();
        boolean hasPdfContentType = "application/pdf".equalsIgnoreCase(contentType);
        boolean hasPdfExtension = fileName != null && fileName.toLowerCase(Locale.ROOT).endsWith(".pdf");

        if (!hasPdfContentType || !hasPdfExtension) {
            throw new BadRequestException("Somente arquivos PDF podem ser anexados como relatorio.");
        }
    }
}

