package com.elcollecte.utilisateur.config;

import com.elcollecte.utilisateur.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
        throws ServletException, IOException {

        // Récupérer depuis l'API Gateway (header propagé)
        String userId      = request.getHeader("X-User-Id");
        String role        = request.getHeader("X-User-Role");
        String permissions = request.getHeader("X-User-Permissions");

        if (userId != null && role != null
            && SecurityContextHolder.getContext().getAuthentication() == null) {

            var auth = new UsernamePasswordAuthenticationToken(
                userId,
                null,
                buildAuthorities(role, permissions)
            );
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        // Fallback: lire le JWT directement (appels internes ou Swagger)
        String authHeader = request.getHeader("Authorization");
        if (userId == null && authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Claims claims = jwtService.validateToken(token);
                @SuppressWarnings("unchecked")
                List<String> permissionCodes = claims.get("permissions", List.class);
                var auth = new UsernamePasswordAuthenticationToken(
                    claims.getSubject(),
                    null,
                    buildAuthorities(claims.get("role", String.class),
                        permissionCodes != null ? String.join(",", permissionCodes) : null)
                );
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (JwtException ignored) {}
        }

        chain.doFilter(request, response);
    }

    /**
     * Construit la liste combinée d'autorités : le rôle legacy (ROLE_xxx,
     * conservé pour compat avec l'ancien code) + les permissions dynamiques
     * du profil (PERM_xxx), à utiliser dans les nouveaux @PreAuthorize.
     */
    private List<GrantedAuthority> buildAuthorities(String role, String permissionsCsv) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (role != null && !role.isBlank()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }
        if (permissionsCsv != null && !permissionsCsv.isBlank()) {
            Arrays.stream(permissionsCsv.split(","))
                .filter(p -> !p.isBlank())
                .forEach(p -> authorities.add(new SimpleGrantedAuthority("PERM_" + p)));
        }
        return authorities;
    }
}
