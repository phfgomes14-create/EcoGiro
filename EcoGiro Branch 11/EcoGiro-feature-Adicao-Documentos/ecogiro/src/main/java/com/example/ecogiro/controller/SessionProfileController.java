package com.example.ecogiro.controller;

import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionProfileController {
    @GetMapping("/api/session/profile")
    public Map<String, String> profile(Authentication authentication) {
        var authorities = authentication.getAuthorities().stream()
            .map(authority -> authority.getAuthority()).toList();
        String role = authorities.contains("ROLE_ADMIN_GERAL") ? "ADMIN_GERAL"
            : authorities.contains("ROLE_ADMIN_SETORIAL") ? "ADMIN_SETORIAL"
            : authorities.contains("ROLE_ADMIN_LEGACY") ? "ADMIN"
            : authorities.contains("ROLE_USER") ? "USER" : "UNKNOWN";
        return Map.of("role", role);
    }
}
