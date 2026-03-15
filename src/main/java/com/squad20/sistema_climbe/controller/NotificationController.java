package com.squad20.sistema_climbe.controller;

import com.squad20.sistema_climbe.entityDTO.NotificationDTO;
import com.squad20.sistema_climbe.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Notificações", description = "Gestão de notificações dos usuários")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "Listar notificações", description = "Retorna todas as notificações")
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> findAll() {
        return ResponseEntity.ok(notificationService.findAll());
    }

    @Operation(summary = "Listar por usuário", description = "Retorna as notificações do usuário informado")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDTO>> findByUserId(
            @Parameter(description = "ID do usuário") @PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.findByUserId(userId));
    }

    @Operation(summary = "Buscar por ID", description = "Retorna uma notificação pelo identificador")
    @GetMapping("/{id}")
    public ResponseEntity<NotificationDTO> findById(
            @Parameter(description = "ID da notificação") @PathVariable Long id) {
        return ResponseEntity.ok(notificationService.findById(id));
    }

    @Operation(summary = "Criar notificação", description = "Cadastra uma nova notificação")
    @PostMapping
    public ResponseEntity<NotificationDTO> save(@Valid @RequestBody NotificationDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.save(dto));
    }

    @Operation(summary = "Atualizar notificação", description = "Atualiza uma notificação existente (parcial)")
    @PatchMapping("/{id}")
    public ResponseEntity<NotificationDTO> update(
            @Parameter(description = "ID da notificação") @PathVariable Long id,
            @RequestBody NotificationDTO dto) {
        return ResponseEntity.ok(notificationService.update(id, dto));
    }

    @Operation(summary = "Excluir notificação", description = "Remove uma notificação pelo ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID da notificação") @PathVariable Long id) {
        notificationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
