package com.gitinbits.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * CORS configuration for the Git in Bits PoC backend.
 *
 * <p>Whitelists only the local Vite frontend ({@code http://localhost:5174}).
 * {@code allowCredentials} is required to allow the session cookie to be
 * sent in cross-origin requests from the SPA.
 *
 * <p>For production, replace the allowed origin with the actual deployed
 * frontend URL and set {@code allowedHeaders} and {@code exposedHeaders}
 * explicitly.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Only the Vite frontend is allowed to make cross-origin requests
        config.setAllowedOrigins(List.of("http://localhost:5174"));

        // Allow the methods used by the frontend
        config.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));

        // Allow all headers the frontend might send
        config.setAllowedHeaders(List.of("*"));

        // Allow the browser to expose the session cookie on cross-origin requests.
        // Required because the frontend (localhost:5174) and backend (localhost:9090)
        // are on different ports.
        config.setAllowCredentials(true);

        // Cache preflight response for 1 hour
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
