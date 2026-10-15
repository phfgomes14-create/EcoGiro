package com.example.ecogiro.controller;

import com.example.ecogiro.model.Role;
import com.example.ecogiro.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    @Value("${app.admin.registration-code}")
    private String adminRegistrationCode;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public void registerUser(@RequestParam String fullName,
                             @RequestParam String cpf,
                             @RequestParam Integer age,
                             @RequestParam String email,
                             @RequestParam String password,
                             HttpServletResponse response) throws IOException {
        try {
            authService.register(fullName, cpf, age, email, password, Role.USER);
            response.sendRedirect("/login.html?registered=1");
        } catch (IllegalArgumentException ex) {
            response.sendRedirect("/cadastro.html?error=" + urlCode(ex.getMessage()));
        }
    }

    @PostMapping("/admin/register")
    public void registerAdmin(@RequestParam String adminId,
                              @RequestParam String fullName,
                              @RequestParam String cpf,
                              @RequestParam Integer age,
                              @RequestParam String email,
                              @RequestParam String password,
                              HttpServletResponse response) throws IOException {
        if (!adminRegistrationCode.equals(adminId)) {
            response.sendRedirect("/admin-cadastro.html?error=" + urlCode("ID administrativo não autorizado."));
            return;
        }
        try {
            authService.register(fullName, cpf, age, email, password, Role.ADMIN);
            response.sendRedirect("/admin-login.html?registered=1");
        } catch (IllegalArgumentException ex) {
            response.sendRedirect("/admin-cadastro.html?error=" + urlCode(ex.getMessage()));
        }
    }

    @PostMapping("/admin/login")
    public void adminLogin(@RequestParam(required = false) String name,
                           @RequestParam String email,
                           @RequestParam String password,
                           HttpServletRequest request,
                           HttpServletResponse response) throws IOException {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email.trim().toLowerCase(), password));
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            if (!isAdmin) {
                response.sendRedirect("/admin-login.html?error=" + urlCode("Esta conta não possui permissão de administrador."));
                return;
            }

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            HttpSessionSecurityContextRepository contextRepository = new HttpSessionSecurityContextRepository();
            contextRepository.saveContext(context, request, response);
            response.sendRedirect("/admin.html");
        } catch (AuthenticationException ex) {
            response.sendRedirect("/admin-login.html?error=1");
        }
    }

    private String urlCode(String value) {
        return java.net.URLEncoder.encode(value == null ? "Erro" : value, java.nio.charset.StandardCharsets.UTF_8);
    }
}
