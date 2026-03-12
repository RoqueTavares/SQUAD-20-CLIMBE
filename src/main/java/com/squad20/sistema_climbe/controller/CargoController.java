package com.squad20.sistema_climbe.controller;

import com.squad20.sistema_climbe.entityDTO.CargoDTO;
import com.squad20.sistema_climbe.service.CargoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cargos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CargoController {

    private final CargoService cargoService;

    @GetMapping
    public ResponseEntity<List<CargoDTO>> listarTodos() {
        return ResponseEntity.ok(cargoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CargoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cargoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<CargoDTO> salvar(@RequestBody CargoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cargoService.salvar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CargoDTO> atualizar(@PathVariable Long id, @RequestBody CargoDTO dto) {
        return ResponseEntity.ok(cargoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        cargoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
