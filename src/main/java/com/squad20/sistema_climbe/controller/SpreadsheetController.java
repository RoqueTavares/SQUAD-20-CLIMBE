package com.squad20.sistema_climbe.controller;

import com.squad20.sistema_climbe.entityDTO.SpreadsheetDTO;
import com.squad20.sistema_climbe.service.SpreadsheetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Planilhas", description = "Gestão de planilhas (Google Sheets)")
@RestController
@RequestMapping("/api/spreadsheets")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SpreadsheetController {

    private final SpreadsheetService spreadsheetService;

    @Operation(summary = "Listar planilhas", description = "Retorna todas as planilhas")
    @GetMapping
    public ResponseEntity<List<SpreadsheetDTO>> findAll() {
        return ResponseEntity.ok(spreadsheetService.findAll());
    }

    @Operation(summary = "Listar por contrato", description = "Retorna as planilhas do contrato informado")
    @GetMapping("/contract/{contractId}")
    public ResponseEntity<List<SpreadsheetDTO>> findByContractId(
            @Parameter(description = "ID do contrato") @PathVariable Long contractId) {
        return ResponseEntity.ok(spreadsheetService.findByContractId(contractId));
    }

    @Operation(summary = "Buscar por ID", description = "Retorna uma planilha pelo identificador")
    @GetMapping("/{id}")
    public ResponseEntity<SpreadsheetDTO> findById(
            @Parameter(description = "ID da planilha") @PathVariable Long id) {
        return ResponseEntity.ok(spreadsheetService.findById(id));
    }

    @Operation(summary = "Criar planilha", description = "Cadastra uma nova planilha")
    @PostMapping
    public ResponseEntity<SpreadsheetDTO> save(@Valid @RequestBody SpreadsheetDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(spreadsheetService.save(dto));
    }

    @Operation(summary = "Atualizar planilha", description = "Atualiza uma planilha existente (parcial)")
    @PatchMapping("/{id}")
    public ResponseEntity<SpreadsheetDTO> update(
            @Parameter(description = "ID da planilha") @PathVariable Long id,
            @RequestBody SpreadsheetDTO dto) {
        return ResponseEntity.ok(spreadsheetService.update(id, dto));
    }

    @Operation(summary = "Excluir planilha", description = "Remove uma planilha pelo ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID da planilha") @PathVariable Long id) {
        spreadsheetService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
