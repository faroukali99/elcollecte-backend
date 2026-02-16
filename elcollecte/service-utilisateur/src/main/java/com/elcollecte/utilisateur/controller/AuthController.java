package com.elcollecte.utilisateur.controller;

import com.elcollecte.utilisateur.dto.AuthResponse;
import com.elcollecte.utilisateur.dto.LoginRequest;
import com.elcollecte.utilisateur.dto.RegisterRequest;
import com.elcollecte.utilisateur.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentification", description = "Login, logout, register, refresh token")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Connexion — retourne access token + refresh token")
    public ResponseEntity<AuthResponse> login(
        @Valid @RequestBody LoginRequest request,
        HttpServletRequest httpRequest) {

        String ip = httpRequest.getRemoteAddr();
        return ResponseEntity.ok(authService.login(request, ip));
    }

    @PostMapping("/register")
    @Operation(summary = "Créer un nouveau compte utilisateur")
    public ResponseEntity<Map<String, String>> register(
        @Valid @RequestBody RegisterRequest request) {

        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of("message", "Compte créé avec succès"));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renouveler l'access token via le refresh token")
    public ResponseEntity<AuthResponse> refresh(
        @RequestHeader("X-Refresh-Token") String refreshToken) {

        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    @PostMapping("/logout")
    @Operation(summary = "Révoquer le refresh token")
    public ResponseEntity<Map<String, String>> logout(
        @RequestHeader("X-User-Id") Long userId) {

        authService.logout(userId);
        return ResponseEntity.ok(Map.of("message", "Déconnexion réussie"));
    }
}
