package com.elcollecte.utilisateur.service;

import com.elcollecte.utilisateur.dto.AuthResponse;
import com.elcollecte.utilisateur.dto.LoginRequest;
import com.elcollecte.utilisateur.dto.RegisterRequest;
import com.elcollecte.utilisateur.entity.Organisation;
import com.elcollecte.utilisateur.entity.User;
import com.elcollecte.utilisateur.repository.OrganisationRepository;
import com.elcollecte.utilisateur.repository.UserRepository;
import com.elcollecte.utilisateur.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Optional;

@Service
public class AuthService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

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

        System.out.println("==========================================");
        System.out.println("DEBUT AUTH SERVICE LOGIN");
        System.out.println("Email reçu : " + request.email());
        System.out.println("==========================================");

        try {

            System.out.println("1. Recherche de AuthenticationManager...");

            AuthenticationManager authenticationManager =
                    authManagerProvider.getObject();

            System.out.println("2. AuthenticationManager trouvé");

            System.out.println("3. Authentification de : " + request.email());

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );

            System.out.println("4. AUTHENTIFICATION OK");

        } catch (AuthenticationException e) {

            System.out.println("❌ AUTHENTIFICATION REFUSÉE");
            System.out.println("Type : " + e.getClass().getName());
            System.out.println("Message : " + e.getMessage());

            publishAuditEvent(
                    "LOGIN_FAILED",
                    null,
                    ipAddress,
                    "Tentative échouée: " + request.email()
            );

            throw new BadCredentialsException(
                    "Identifiants incorrects"
            );
        }

        System.out.println("5. Recherche utilisateur en base...");

        Optional<User> userOptional =
                userRepository.findByEmailAndActiveTrue(request.email());

        System.out.println(
                "6. Utilisateur trouvé ? " + userOptional.isPresent()
        );

        if (userOptional.isEmpty()) {

            System.out.println(
                    "❌ Aucun utilisateur actif trouvé pour : "
                            + request.email()
            );

            throw new BadCredentialsException(
                    "Compte inactif ou introuvable"
            );
        }

        User user = userOptional.get();

        System.out.println("7. Utilisateur chargé");
        System.out.println("ID       : " + user.getId());
        System.out.println("Email    : " + user.getEmail());
        System.out.println("Nom      : " + user.getNom());
        System.out.println("Prénom   : " + user.getPrenom());
        System.out.println("Role     : " + user.getRole());
        System.out.println("Active   : " + user.isActive());

        System.out.println("8. Génération access token...");

        String accessToken =
                jwtService.generateAccessToken(user);

        System.out.println("9. Access token généré");

        System.out.println("10. Génération refresh token...");

        String refreshToken =
                jwtService.generateRefreshToken(user);

        System.out.println("11. Refresh token généré");

        userRepository.updateRefreshToken(
                user.getId(),
                passwordEncoder.encode(refreshToken)
        );

        System.out.println("12. Refresh token enregistré");

        userRepository.updateLastLogin(
                user.getId(),
                LocalDateTime.now()
        );

        System.out.println("13. Last login enregistré");

        publishAuditEvent(
                "LOGIN_SUCCESS",
                user.getId(),
                ipAddress,
                null
        );

        System.out.println("14. Audit envoyé");

        AuthResponse response = new AuthResponse(
                accessToken,
                refreshToken,
                jwtService.getAccessTokenExpirationSeconds(),
                new AuthResponse.UserInfo(
                        user.getId(),
                        user.getNom(),
                        user.getPrenom(),
                        user.getEmail(),
                        user.getRole().name(),
                        user.getOrganisation() != null
                                ? user.getOrganisation().getId()
                                : null
                )
        );

        System.out.println("==========================================");
        System.out.println("LOGIN SUCCESS");
        System.out.println("==========================================");

        return response;
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
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("action",    action);
            event.put("userId",    userId);
            event.put("ipAddress", ipAddress);
            event.put("details",   details);
            event.put("timestamp", LocalDateTime.now().toString());
            kafkaTemplate.send("audit.events", event);
        } catch (Exception e) {
            log.warn("Impossible d'envoyer l'événement d'audit à Kafka: {}", e.getMessage());
        }
    }
}
