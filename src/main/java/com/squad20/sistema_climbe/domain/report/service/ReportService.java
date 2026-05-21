package com.squad20.sistema_climbe.domain.report.service;

import com.squad20.sistema_climbe.domain.report.dto.ReportCreateRequest;
import com.squad20.sistema_climbe.domain.report.dto.ReportDTO;
import com.squad20.sistema_climbe.domain.report.dto.ReportPatchRequest;
import com.squad20.sistema_climbe.domain.report.entity.Report;
import com.squad20.sistema_climbe.domain.report.mapper.ReportMapper;
import com.squad20.sistema_climbe.domain.report.repository.ReportRepository;
import com.squad20.sistema_climbe.domain.contract.entity.Contract;
import com.squad20.sistema_climbe.exception.ResourceNotFoundException;
import com.squad20.sistema_climbe.domain.contract.repository.ContractRepository;
import com.squad20.sistema_climbe.domain.user.entity.User;
import com.squad20.sistema_climbe.domain.user.repository.UserRepository;
import com.squad20.sistema_climbe.domain.report.entity.ReportStatus;
import com.squad20.sistema_climbe.domain.notification.service.NotificationService;
import com.squad20.sistema_climbe.domain.notification.dto.NotificationCreateRequest;
import com.squad20.sistema_climbe.domain.service.entity.OfferedService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ContractRepository contractRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final ReportMapper reportMapper;

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
}

