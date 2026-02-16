package com.elcollecte.utilisateur.service;

import com.elcollecte.utilisateur.dto.AuthResponse;
import com.elcollecte.utilisateur.dto.LoginRequest;
import com.elcollecte.utilisateur.dto.RegisterRequest;
import com.elcollecte.utilisateur.entity.Organisation;
import com.elcollecte.utilisateur.entity.User;
import com.elcollecte.utilisateur.repository.OrganisationRepository;
import com.elcollecte.utilisateur.repository.UserRepository;
import com.elcollecte.utilisateur.security.JwtService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService implements UserDetailsService {

    private final UserRepository         userRepository;
    private final OrganisationRepository orgRepository;
    private final JwtService             jwtService;
    private final PasswordEncoder        passwordEncoder;
    private final ObjectProvider<AuthenticationManager> authManagerProvider;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public AuthService(UserRepository userRepository,
                       OrganisationRepository orgRepository,
                       JwtService jwtService,
                       PasswordEncoder passwordEncoder,
                       ObjectProvider<AuthenticationManager> authManagerProvider,
                       KafkaTemplate<String, Object> kafkaTemplate) {
        this.userRepository  = userRepository;
        this.orgRepository   = orgRepository;
        this.jwtService      = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authManagerProvider = authManagerProvider;
        this.kafkaTemplate   = kafkaTemplate;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmailAndActiveTrue(email)
            .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable: " + email));
    }

    @Transactional
    public AuthResponse login(LoginRequest request, String ipAddress) {
        try {
            authManagerProvider.getObject().authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.email(), request.password())
            );
        } catch (AuthenticationException e) {
            publishAuditEvent("LOGIN_FAILED", null, ipAddress,
                "Tentative échouée: " + request.email());
            throw new BadCredentialsException("Identifiants incorrects");
        }

        User user = userRepository.findByEmailAndActiveTrue(request.email())
            .orElseThrow(() -> new BadCredentialsException("Compte inactif ou introuvable"));

        String accessToken  = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        userRepository.updateRefreshToken(user.getId(),
            passwordEncoder.encode(refreshToken));
        userRepository.updateLastLogin(user.getId(), LocalDateTime.now());

        publishAuditEvent("LOGIN_SUCCESS", user.getId(), ipAddress, null);

        return new AuthResponse(
            accessToken,
            refreshToken,
            jwtService.getAccessTokenExpirationSeconds(),
            new AuthResponse.UserInfo(
                user.getId(),
                user.getNom(),
                user.getPrenom(),
                user.getEmail(),
                user.getRole().name(),
                user.getOrganisation() != null ? user.getOrganisation().getId() : null
            )
        );
    }

    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        Long userId = jwtService.extractUserId(refreshToken);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BadCredentialsException("Utilisateur introuvable"));

        if (user.getRefreshToken() == null
            || !passwordEncoder.matches(refreshToken, user.getRefreshToken())) {
            throw new BadCredentialsException("Refresh token invalide ou révoqué");
        }

        String newAccess  = jwtService.generateAccessToken(user);
        String newRefresh = jwtService.generateRefreshToken(user);

        userRepository.updateRefreshToken(user.getId(),
            passwordEncoder.encode(newRefresh));

        return new AuthResponse(
            newAccess, newRefresh,
            jwtService.getAccessTokenExpirationSeconds(),
            null
        );
    }

    @Transactional
    public void register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("Email déjà utilisé: " + req.email());
        }

        Organisation org = orgRepository.findById(req.organisationId())
            .orElseThrow(() -> new IllegalArgumentException(
                "Organisation introuvable: " + req.organisationId()));

        User user = new User(
            req.nom(),
            req.prenom(),
            req.email(),
            passwordEncoder.encode(req.password()),
            User.Role.valueOf(req.role().toUpperCase()),
            org
        );

        userRepository.save(user);
        publishAuditEvent("USER_CREATED", user.getId(), null,
            "Nouvel utilisateur: " + req.email());
    }

    @Transactional
    public void logout(Long userId) {
        userRepository.updateRefreshToken(userId, null);
        publishAuditEvent("LOGOUT", userId, null, null);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void publishAuditEvent(String action, Long userId,
                                   String ipAddress, String details) {
        Map<String, Object> event = new HashMap<>();
        event.put("action",    action);
        event.put("userId",    userId);
        event.put("ipAddress", ipAddress);
        event.put("details",   details);
        event.put("timestamp", LocalDateTime.now().toString());
        kafkaTemplate.send("audit.events", event);
    }
}
