package com.squad20.sistema_climbe.controller;


import com.squad20.sistema_climbe.dto.AuthenticationRequest;
import com.squad20.sistema_climbe.dto.AuthenticationResponse;
import com.squad20.sistema_climbe.dto.RegisterRequest;
import com.squad20.sistema_climbe.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@Tag(name = "Auth", description = "Authenticação do sistema")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @Operation(summary = "Register", description = "Rota para registra Usuarios")
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    @Operation(summary = "Login", description = "Rota para logar Usuarios")
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @Valid @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }
}
