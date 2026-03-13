package com.squad20.sistema_climbe.controller;

import com.squad20.sistema_climbe.entity.Enterprise;
import com.squad20.sistema_climbe.dto.EnterpriseDTO;
import com.squad20.sistema_climbe.service.EnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Empresas", description = "Cadastro e consulta de empresas")
@RestController
@RequestMapping("/api/enterprises")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EnterpriseController {

    private final EnterpriseService enterpriseService;

    @Operation(summary = "Listar empresas", description = "Retorna todas as empresas cadastradas")
    @GetMapping
    public ResponseEntity<List<EnterpriseDTO>> findAll() {
        return ResponseEntity.ok(enterpriseService.findAll());
    }

    @Operation(summary = "Buscar por ID", description = "Retorna uma empresa pelo identificador")
    @GetMapping("/{id}")
    public ResponseEntity<EnterpriseDTO> findById(
            @Parameter(description = "ID da empresa") @PathVariable int id) {
        return ResponseEntity.ok(enterpriseService.findById(id));
    }

    @Operation(summary = "Buscar por e-mail", description = "Retorna a empresa com o e-mail informado")
    @GetMapping("/email/{email}")
    public ResponseEntity<EnterpriseDTO> findByEmail(
            @Parameter(description = "E-mail da empresa") @PathVariable String email) {
        return ResponseEntity.ok(enterpriseService.findByEmail(email));
    }

    @Operation(summary = "Buscar por CNPJ", description = "Retorna a empresa com o CNPJ informado")
    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<EnterpriseDTO> findByCnpj(
            @Parameter(description = "CNPJ da empresa") @PathVariable String cnpj) {
        return ResponseEntity.ok(enterpriseService.findByCnpj(cnpj));
    }

    @Operation(summary = "Criar empresa", description = "Cadastra uma nova empresa")
    @PostMapping
    public ResponseEntity<Enterprise> save(@RequestBody Enterprise enterprise) {
        Enterprise saved = enterpriseService.save(enterprise);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(summary = "Atualizar empresa", description = "Atualiza uma empresa existente (parcial)")
    @PatchMapping("/{id}")
    public ResponseEntity<Enterprise> update(
            @Parameter(description = "ID da empresa") @PathVariable int id,
            @RequestBody Enterprise enterprise) {
        Enterprise updated = enterpriseService.update(id, enterprise);
        return ResponseEntity.ok(updated);
    }
}
