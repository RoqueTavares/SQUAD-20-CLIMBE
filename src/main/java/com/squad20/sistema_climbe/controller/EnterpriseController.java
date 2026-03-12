package com.squad20.sistema_climbe.controller;

import com.squad20.sistema_climbe.entity.Enterprise;
import com.squad20.sistema_climbe.entityDTO.EnterpriseDTO;
import com.squad20.sistema_climbe.service.EnterpriseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/enterprise")
@RequiredArgsConstructor
public class EnterpriseController {

    private final EnterpriseService enterpriseService;

    @GetMapping
    public ResponseEntity<List<EnterpriseDTO>> findAll() {

        return ResponseEntity.ok(enterpriseService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnterpriseDTO> findById(@PathVariable int id) {
        return ResponseEntity.ok(enterpriseService.findById(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<EnterpriseDTO> findByEmail(@PathVariable String email) {
        return ResponseEntity.ok(enterpriseService.findByEmail(email));
    }

    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<EnterpriseDTO> findByCnpj(@PathVariable String cnpj) {
        return ResponseEntity.ok(enterpriseService.findByCnpj(cnpj));
    }

    @PostMapping
    public ResponseEntity<Enterprise> save(@RequestBody Enterprise enterprise) {
        Enterprise savedEnterprise = enterpriseService.save(enterprise);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEnterprise);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Enterprise> update(@PathVariable int id, @RequestBody Enterprise enterprise) {
        Enterprise updatedEnterprise = enterpriseService.update(id, enterprise);
        return ResponseEntity.ok(updatedEnterprise);
    }
}
