package com.elcollecte.utilisateur.security;

import com.elcollecte.utilisateur.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private static final long ACCESS_TOKEN_EXPIRATION  = 3_600_000L;   // 1h
    private static final long REFRESH_TOKEN_EXPIRATION = 604_800_000L; // 7j

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user) {
        // "role" (legacy) reste émis pour compatibilité descendante avec
        // les services qui comparent encore cette String directement
        // (ex: ProjetService.findAll, CollecteService.findAll). "profil" et
        // "permissions" sont les nouveaux claims dynamiques à privilégier
        // dans tout nouveau code d'autorisation.
        List<String> permissionCodes = (user.getProfil() != null)
            ? user.getProfil().getProfilPermissions().stream()
                .map(pp -> pp.getPermission().getCode())
                .toList()
            : List.of();

        return Jwts.builder()
            .subject(String.valueOf(user.getId()))
            .claim("email",       user.getEmail())
            .claim("role",        user.getRole().name())
            .claim("profil",      user.getProfil() != null ? user.getProfil().getCode() : null)
            .claim("permissions", permissionCodes)
            .claim("orgId",  user.getOrganisation() != null
                             ? user.getOrganisation().getId() : null)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
            .signWith(getKey())
            .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
            .subject(String.valueOf(user.getId()))
            .claim("type", "refresh")
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
            .signWith(getKey())
            .compact();
    }

    public Claims validateToken(String token) {
        return Jwts.parser()
            .verifyWith(getKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = validateToken(token);
            return !claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            return false;
        }
    }

    public Long extractUserId(String token) {
        return Long.valueOf(validateToken(token).getSubject());
    }

    public long getAccessTokenExpirationSeconds() {
        return ACCESS_TOKEN_EXPIRATION / 1000;
    }
}
