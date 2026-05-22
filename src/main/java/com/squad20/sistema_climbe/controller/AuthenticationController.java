package com.squad20.sistema_climbe.controller;


import com.squad20.sistema_climbe.config.AuthCookieFactory;
import com.squad20.sistema_climbe.dto.AuthenticationRequest;
import com.squad20.sistema_climbe.dto.AuthenticationResponse;
import com.squad20.sistema_climbe.dto.RequestAccessRequest;
import com.squad20.sistema_climbe.dto.TokenRefreshResponse;
import com.squad20.sistema_climbe.domain.user.dto.UserDTO;
import com.squad20.sistema_climbe.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    private final AuthCookieFactory authCookieFactory;


    @Operation(summary = "Login", description = "Rota para logar Usuarios")
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @Valid @RequestBody AuthenticationRequest request
    ) {
        AuthenticationResponse response = service.authenticate(request);
        return ResponseEntity.ok()
                .headers(authCookieFactory.createAuthCookies(response.getToken(), response.getRefreshToken()))
                .body(response);
    }

    @Operation(summary = "Refresh Token", description = "Gera um novo Access Token a partir de um Refresh Token no Cookie")
    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponse> refreshToken(
            @org.springframework.web.bind.annotation.CookieValue(name = "refreshToken") String refreshToken
    ) {
        TokenRefreshResponse response = service.refreshToken(refreshToken);
        
        ResponseCookie accessCookie = authCookieFactory.accessTokenCookie(response.getAccessToken(), 30 * 60);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .body(response);
    }

    @Operation(summary = "Logout", description = "Desloga o usuário e limpa o Refresh Token")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent()
                .headers(authCookieFactory.clearAuthCookies())
                .build();
    }

    @Operation(summary = "Solicitar acesso", description = "Solicita aprovação de acesso para um usuário pendente")
    @PostMapping("/request-access")
    public ResponseEntity<Void> requestAccess(@Valid @RequestBody RequestAccessRequest request) {
        service.requestAccess(request.getEmail());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Obter usuário logado", description = "Retorna os dados do usuário autenticado na sessão")
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMe() {
        UserDTO user = service.getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(user);
    }

}
