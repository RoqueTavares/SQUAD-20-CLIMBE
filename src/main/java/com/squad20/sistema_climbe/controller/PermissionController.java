package com.squad20.sistema_climbe.controller;

import com.squad20.sistema_climbe.entityDTO.PermissionDTO;
import com.squad20.sistema_climbe.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Tag(name = "Permissões", description = "Gestão de permissões de acesso dos usuários")
@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PermissionController {

    private final PermissionService permissionService;

    @Operation(summary = "Listar permissões", description = "Retorna todas as permissões cadastradas")
    @GetMapping
    public ResponseEntity<List<PermissionDTO>> findAll() {
        return ResponseEntity.ok(permissionService.findAll());
    }

    @Operation(summary = "Buscar permissão por ID", description = "Retorna uma permissão pelo identificador")
    @GetMapping("/{id}")
    public ResponseEntity<PermissionDTO> findById(
            @Parameter(description = "ID da permissão") @PathVariable Long id) {
        return ResponseEntity.ok(permissionService.findById(id));
    }

    @Operation(summary = "Criar permissão", description = "Cadastra uma nova permissão")
    @PostMapping
    public ResponseEntity<PermissionDTO> save(@Valid @RequestBody PermissionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(permissionService.save(dto));
    }

    @Operation(summary = "Atualizar permissão", description = "Atualiza uma permissão existente (parcial)")
    @PatchMapping("/{id}")
    public ResponseEntity<PermissionDTO> update(
            @Parameter(description = "ID da permissão") @PathVariable Long id,
            @RequestBody PermissionDTO dto) {
        return ResponseEntity.ok(permissionService.update(id, dto));
    }

    @Operation(summary = "Excluir permissão", description = "Remove uma permissão pelo ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID da permissão") @PathVariable Long id) {
        permissionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
