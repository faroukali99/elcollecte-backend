
package com.elcollecte.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {

        CorsConfiguration corsConfig = new CorsConfiguration();

        /*
         * ============================================================
         * ORIGINES AUTORISÉES
         * ============================================================
         */

        corsConfig.setAllowedOrigins(Arrays.asList(

                // Vite
                "http://localhost:5173",
                "http://localhost:5174",

                // CRA
                "http://localhost:3000",

                // Local
                "http://localhost",
                "http://localhost:80",

                // Réseau local
                "http://192.168.56.1:5173",
                "http://192.168.56.1:5174",
                "http://192.168.56.1:80",
                "http://192.168.56.1",

                // ====================================================
                // DEV TUNNEL
                // ====================================================
                "https://0qjpzb9f-5173.uks1.devtunnels.ms"
        ));


        /*
         * ============================================================
         * MÉTHODES HTTP
         * ============================================================
         */

        corsConfig.setAllowedMethods(Arrays.asList(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS",
                "HEAD"
        ));


        /*
         * ============================================================
         * HEADERS AUTORISÉS
         * ============================================================
         */

        corsConfig.setAllowedHeaders(Arrays.asList(
                "*"
        ));


        /*
         * ============================================================
         * HEADERS EXPOSÉS AU FRONTEND
         * ============================================================
         */

        corsConfig.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Disposition"
        ));


        /*
         * ============================================================
         * COOKIES / CREDENTIALS
         * ============================================================
         */

        corsConfig.setAllowCredentials(true);


        /*
         * ============================================================
         * CACHE DU PREFLIGHT
         * ============================================================
         */

        corsConfig.setMaxAge(3600L);


        /*
         * ============================================================
         * APPLICATION À TOUTES LES ROUTES
         * ============================================================
         */

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                corsConfig
        );


        return new CorsWebFilter(source);
    }
}

