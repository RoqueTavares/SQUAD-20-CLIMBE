package com.squad20.sistema_climbe.service;

import com.squad20.sistema_climbe.entity.Contract;
import com.squad20.sistema_climbe.entity.Report;
import com.squad20.sistema_climbe.entityDTO.ReportDTO;
import com.squad20.sistema_climbe.exception.ResourceNotFoundException;
import com.squad20.sistema_climbe.repository.ContractRepository;
import com.squad20.sistema_climbe.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ContractRepository contractRepository;

    @Transactional(readOnly = true)
    public List<ReportDTO> findAll() {
        return reportRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReportDTO> findByContractId(Long contractId) {
        return reportRepository.findByContract_Id(contractId).stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReportDTO findById(Long id) {
        Report report = findReportOrThrow(id);
        return toDTO(report);
    }

    @Transactional
    public ReportDTO save(ReportDTO dto) {
        Contract contract = contractRepository.findById(dto.getContractId())
                .orElseThrow(() -> new ResourceNotFoundException("Contrato não encontrado com id: " + dto.getContractId()));
        Report report = toEntity(dto, contract);
        report = reportRepository.save(report);
        return toDTO(report);
    }

    @Transactional
    public ReportDTO update(Long id, ReportDTO dto) {
        Report existing = findReportOrThrow(id);
        if (dto.getPdfUrl() != null) existing.setPdfUrl(dto.getPdfUrl());
        if (dto.getSentAt() != null) existing.setSentAt(dto.getSentAt());
        if (dto.getContractId() != null) {
            Contract contract = contractRepository.findById(dto.getContractId())
                    .orElseThrow(() -> new ResourceNotFoundException("Contrato não encontrado com id: " + dto.getContractId()));
            existing.setContract(contract);
        }
        existing = reportRepository.save(existing);
        return toDTO(existing);
    }

    @Transactional
    public void delete(Long id) {
        Report report = findReportOrThrow(id);
        reportRepository.delete(report);
    }

    private Report findReportOrThrow(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Relatório não encontrado com id: " + id));
    }

    private ReportDTO toDTO(Report r) {
        return ReportDTO.builder()
                .id(r.getId())
                .contractId(r.getContract() != null ? r.getContract().getId() : null)
                .pdfUrl(r.getPdfUrl())
                .sentAt(r.getSentAt())
                .build();
    }

    private Report toEntity(ReportDTO dto, Contract contract) {
        return Report.builder()
                .contract(contract)
                .pdfUrl(dto.getPdfUrl())
                .sentAt(dto.getSentAt())
                .build();
    }
}
