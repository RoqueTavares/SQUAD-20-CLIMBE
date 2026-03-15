package com.squad20.sistema_climbe.service;

import com.squad20.sistema_climbe.entity.Contract;
import com.squad20.sistema_climbe.entity.Spreadsheet;
import com.squad20.sistema_climbe.entityDTO.SpreadsheetDTO;
import com.squad20.sistema_climbe.exception.ResourceNotFoundException;
import com.squad20.sistema_climbe.repository.ContractRepository;
import com.squad20.sistema_climbe.repository.SpreadsheetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpreadsheetService {

    private final SpreadsheetRepository spreadsheetRepository;
    private final ContractRepository contractRepository;

    @Transactional(readOnly = true)
    public List<SpreadsheetDTO> findAll() {
        return spreadsheetRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SpreadsheetDTO> findByContractId(Long contractId) {
        return spreadsheetRepository.findByContract_Id(contractId).stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public SpreadsheetDTO findById(Long id) {
        Spreadsheet spreadsheet = findSpreadsheetOrThrow(id);
        return toDTO(spreadsheet);
    }

    @Transactional
    public SpreadsheetDTO save(SpreadsheetDTO dto) {
        Contract contract = contractRepository.findById(dto.getContractId())
                .orElseThrow(() -> new ResourceNotFoundException("Contrato não encontrado com id: " + dto.getContractId()));
        Spreadsheet spreadsheet = toEntity(dto, contract);
        spreadsheet = spreadsheetRepository.save(spreadsheet);
        return toDTO(spreadsheet);
    }

    @Transactional
    public SpreadsheetDTO update(Long id, SpreadsheetDTO dto) {
        Spreadsheet existing = findSpreadsheetOrThrow(id);
        if (dto.getGoogleSheetsUrl() != null) existing.setGoogleSheetsUrl(dto.getGoogleSheetsUrl());
        if (dto.getLocked() != null) existing.setLocked(dto.getLocked());
        if (dto.getViewPermission() != null) existing.setViewPermission(dto.getViewPermission());
        if (dto.getContractId() != null) {
            Contract contract = contractRepository.findById(dto.getContractId())
                    .orElseThrow(() -> new ResourceNotFoundException("Contrato não encontrado com id: " + dto.getContractId()));
            existing.setContract(contract);
        }
        existing = spreadsheetRepository.save(existing);
        return toDTO(existing);
    }

    @Transactional
    public void delete(Long id) {
        Spreadsheet spreadsheet = findSpreadsheetOrThrow(id);
        spreadsheetRepository.delete(spreadsheet);
    }

    private Spreadsheet findSpreadsheetOrThrow(Long id) {
        return spreadsheetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Planilha não encontrada com id: " + id));
    }

    private SpreadsheetDTO toDTO(Spreadsheet s) {
        return SpreadsheetDTO.builder()
                .id(s.getId())
                .contractId(s.getContract() != null ? s.getContract().getId() : null)
                .googleSheetsUrl(s.getGoogleSheetsUrl())
                .locked(s.getLocked())
                .viewPermission(s.getViewPermission())
                .build();
    }

    private Spreadsheet toEntity(SpreadsheetDTO dto, Contract contract) {
        return Spreadsheet.builder()
                .contract(contract)
                .googleSheetsUrl(dto.getGoogleSheetsUrl())
                .locked(dto.getLocked())
                .viewPermission(dto.getViewPermission())
                .build();
    }
}
