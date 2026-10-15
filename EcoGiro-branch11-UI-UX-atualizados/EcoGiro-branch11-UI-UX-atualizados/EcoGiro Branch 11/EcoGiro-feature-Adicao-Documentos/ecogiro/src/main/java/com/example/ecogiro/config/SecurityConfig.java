package com.example.ecogiro.config;

import com.example.ecogiro.model.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .headers(headers ->
                headers.frameOptions(frame -> frame.sameOrigin())
            )

            .authorizeHttpRequests(auth -> auth

                // Rotas públicas
                .requestMatchers(
                    "/",
                    "/index.html",
                    "/login.html",
                    "/cadastro.html",
                    "/admin-login.html",
                    "/admin-cadastro.html",
                    "/quiz.html",
                    "/403.html",
                    "/assets/**",
                    "/register",
                    "/admin/register",
                    "/admin/login",
                    "/api/recommendations",
                    "/h2-console/**",
                    "/error",
                    "/favicon.ico"
                ).permitAll()

                // Rotas exclusivas de ADMIN
                .requestMatchers(
                    "/admin.html",
                    "/api/admin/**"
                ).hasRole(Role.ADMIN.name())

                // Rotas exclusivas de USER
                .requestMatchers(
                    "/usuario.html",
                    "/planos-aluguel.html",
                    "/api/user/**"
                ).hasRole(Role.USER.name())

                // Rotas compartilhadas entre usuários autenticados
                .requestMatchers(
                    "/mapa.html",
                    "/api/map/**"
                ).authenticated()

                .anyRequest().permitAll()
            )

            // Login do usuário comum
            .formLogin(form -> form
                .loginPage("/login.html")
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")

                .successHandler((request, response, authentication) -> {

                    boolean isUser = authentication
                        .getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                            authority.getAuthority().equals("ROLE_USER")
                        );

                    // Impede ADMIN de entrar pelo login de usuário
                    if (!isUser) {

                        SecurityContextHolder.clearContext();

                        if (request.getSession(false) != null) {
                            request.getSession(false).invalidate();
                        }

                        response.sendRedirect(
                            "/login.html?error=Esta+conta+e+administrativa.+Use+o+acesso+de+administrador."
                        );

                        return;
                    }

                    response.sendRedirect("/usuario.html");
                })

                .failureUrl("/login.html?error=1")
                .permitAll()
            )

            // Página personalizada para acesso sem permissão
            .exceptionHandling(exception ->
                exception.accessDeniedPage("/403.html")
            )

            // Logout
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login.html?logout=1")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            );

        return http.build();
    }
}