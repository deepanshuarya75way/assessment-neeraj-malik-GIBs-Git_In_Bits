package com.gitinbits.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

/**
 * Spring Security configuration.
 *
 * <p><b>Authentication</b>: GitHub OAuth2 login. Spring Security handles the
 * full OAuth2 Authorization Code flow automatically. No passwords, no JWTs.
 *
 * <p><b>Session</b>: Standard server-side session stored in memory (no database).
 * The {@code OAuth2AuthorizedClientService} (auto-configured as in-memory)
 * stores the OAuth2 access token server-side, keyed by principal name.
 *
 * <p><b>CSRF</b>: Disabled. This backend is a stateless-by-design REST API
 * consumed by a SPA frontend. CSRF protection is not applicable for APIs
 * where the session cookie is never used for state-changing HTML form submissions.
 * If this evolves into a server-rendered app, CSRF must be re-enabled.
 *
 * <p><b>Logout</b>: Configured at {@code POST /api/auth/logout}.
 * Invalidates the session, clears the security context, and deletes the session cookie.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * The URL the frontend should redirect to after OAuth2 login succeeds.
     * The frontend reads the session cookie set during this redirect and then
     * calls GET /api/auth/me to confirm the session.
     */
    private static final String FRONTEND_CALLBACK_URL = "http://localhost:5174/auth/callback";

    /**
     * The URL the browser is redirected to after logout.
     */
    private static final String FRONTEND_BASE_URL = "http://localhost:5174";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ClientRegistrationRepository clientRegistrationRepository) throws Exception {
        
        // Custom resolver to force GitHub to show the account picker
        DefaultOAuth2AuthorizationRequestResolver resolver = 
                new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization");
        
        resolver.setAuthorizationRequestCustomizer(customizer -> {
            customizer.additionalParameters(params -> params.put("prompt", "select_account"));
        });

        http
                // ── CORS ─────────────────────────────────────────────────────
                // Uses the CorsConfig bean (configured for localhost:5174 only)
                .cors(Customizer.withDefaults())

                // ── CSRF ──────────────────────────────────────────────────────
                .csrf(AbstractHttpConfigurer::disable)

                // ── Authorization Rules ───────────────────────────────────────
                .authorizeHttpRequests(auth -> auth
                        // Spring Security's OAuth2 endpoints — must be public
                        .requestMatchers("/login/**", "/oauth2/**").permitAll()
                        // Actuator health (if added later)
                        .requestMatchers("/actuator/health").permitAll()
                        // Error endpoint
                        .requestMatchers("/error").permitAll()
                        // All application API endpoints require a valid GitHub session
                        .anyRequest().authenticated()
                )

                // ── OAuth2 Login ──────────────────────────────────────────────
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(auth -> auth
                                .authorizationRequestResolver(resolver)
                        )
                        // After successful GitHub login, redirect the browser to
                        // the frontend callback URL. The session cookie is set
                        // on this redirect, making it available to the SPA.
                        .defaultSuccessUrl(FRONTEND_CALLBACK_URL, true)
                        // On login failure, redirect to frontend with an error indicator
                        .failureUrl("http://localhost:5174/auth/error")
                )

                // ── Logout ────────────────────────────────────────────────────
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(200);
                        })
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                );

        return http.build();
    }
}
