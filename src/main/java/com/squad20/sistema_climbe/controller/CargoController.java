package com.squad20.sistema_climbe.controller;

import com.squad20.sistema_climbe.entityDTO.CargoDTO;
import com.squad20.sistema_climbe.service.CargoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Tag(name = "Cargos", description = "Gestão de cargos (funções) dos usuários")
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CargoController {

    private final CargoService cargoService;

    @Operation(summary = "Listar cargos", description = "Retorna todos os cargos cadastrados")
    @GetMapping
    public ResponseEntity<List<CargoDTO>> findAll() {
        return ResponseEntity.ok(cargoService.findAll());
    }

    @Operation(summary = "Buscar por ID", description = "Retorna um cargo pelo identificador")
    @GetMapping("/{id}")
    public ResponseEntity<CargoDTO> findById(
            @Parameter(description = "ID do cargo") @PathVariable Long id) {
        return ResponseEntity.ok(cargoService.findById(id));
    }

    @Operation(summary = "Criar cargo", description = "Cadastra um novo cargo")
    @PostMapping
    public ResponseEntity<CargoDTO> save(@Valid @RequestBody CargoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cargoService.save(dto));
    }

    @Operation(summary = "Atualizar cargo", description = "Atualiza um cargo existente (parcial)")
    @PatchMapping("/{id}")
    public ResponseEntity<CargoDTO> update(
            @Parameter(description = "ID do cargo") @PathVariable Long id,
            @RequestBody CargoDTO dto) {
        return ResponseEntity.ok(cargoService.update(id, dto));
    }

    @Operation(summary = "Excluir cargo", description = "Remove um cargo pelo ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do cargo") @PathVariable Long id) {
        cargoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
