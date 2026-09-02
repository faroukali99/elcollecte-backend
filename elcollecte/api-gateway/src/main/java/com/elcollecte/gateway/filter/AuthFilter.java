package com.elcollecte.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/refresh",
            "/api/auth/forgot-password",
            "/api/auth/reset-password"
    );

    public AuthFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {

        return (exchange, chain) -> {

            String path = exchange.getRequest()
                    .getURI()
                    .getPath();

            /*
             * ============================================================
             * 1. CORS PREFLIGHT
             * ============================================================
             *
             * Le navigateur envoie une requête OPTIONS avant le POST.
             *
             * Exemple :
             *
             * OPTIONS /api/auth/login
             *
             * Cette requête ne contient généralement pas de JWT.
             * Elle doit donc être autorisée immédiatement.
             */
            if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
                return chain.filter(exchange);
            }


            /*
             * ============================================================
             * 2. ROUTES PUBLIQUES
             * ============================================================
             *
             * Ces endpoints ne nécessitent pas de JWT.
             */
            if (PUBLIC_PATHS.stream().anyMatch(path::startsWith)) {
                return chain.filter(exchange);
            }


            /*
             * ============================================================
             * 3. RÉCUPÉRATION DU TOKEN
             * ============================================================
             */
            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {

                return onError(
                        exchange,
                        HttpStatus.UNAUTHORIZED,
                        "Token manquant"
                );
            }


            String token = authHeader.substring(7);


            /*
             * ============================================================
             * 4. VALIDATION DU JWT
             * ============================================================
             */
            try {

                SecretKey key = Keys.hmacShaKeyFor(
                        jwtSecret.getBytes(StandardCharsets.UTF_8)
                );

                Claims claims = Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();


                /*
                 * ========================================================
                 * 5. RÉCUPÉRATION DE L'ORGANISATION
                 * ========================================================
                 */
                Object orgIdObj = claims.get("orgId");

                String orgIdStr =
                        (orgIdObj != null
                                && !orgIdObj.toString().equals("null"))
                                ? orgIdObj.toString()
                                : "1";


                /*
                 * ========================================================
                 * 6. PROPAGATION DES INFORMATIONS UTILISATEUR
                 * ========================================================
                 */
                ServerHttpRequest.Builder requestBuilder =
                        exchange.getRequest()
                                .mutate()
                                .header(
                                        "X-User-Id",
                                        claims.getSubject()
                                )
                                .header(
                                        "X-User-Role",
                                        safeHeader(
                                                claims.get(
                                                        "role",
                                                        String.class
                                                )
                                        )
                                )
                                .header(
                                        "X-User-Email",
                                        safeHeader(
                                                claims.get(
                                                        "email",
                                                        String.class
                                                )
                                        )
                                )
                                .header(
                                        "X-Org-Id",
                                        orgIdStr
                                );


                /*
                 * ========================================================
                 * 7. CONTINUER VERS LE MICRO-SERVICE
                 * ========================================================
                 */
                return chain.filter(
                        exchange.mutate()
                                .request(requestBuilder.build())
                                .build()
                );


            } catch (ExpiredJwtException e) {

                return onError(
                        exchange,
                        HttpStatus.UNAUTHORIZED,
                        "Token expiré"
                );

            } catch (JwtException e) {

                return onError(
                        exchange,
                        HttpStatus.UNAUTHORIZED,
                        "Token invalide"
                );
            }
        };
    }


    private String safeHeader(String value) {

        return value != null ? value : "";
    }


    private Mono<Void> onError(
            ServerWebExchange exchange,
            HttpStatus status,
            String message) {

        ServerHttpResponse response = exchange.getResponse();

        response.setStatusCode(status);

        response.getHeaders().add(
                HttpHeaders.CONTENT_TYPE,
                "application/json;charset=UTF-8"
        );

        byte[] bytes = (
                "{\"success\":false,\"message\":\""
                        + message
                        + "\"}"
        ).getBytes(StandardCharsets.UTF_8);

        org.springframework.core.io.buffer.DataBuffer buffer =
                response.bufferFactory().wrap(bytes);

        return response.writeWith(
                Mono.just(buffer)
        );
    }


    public static class Config {
    }
}

