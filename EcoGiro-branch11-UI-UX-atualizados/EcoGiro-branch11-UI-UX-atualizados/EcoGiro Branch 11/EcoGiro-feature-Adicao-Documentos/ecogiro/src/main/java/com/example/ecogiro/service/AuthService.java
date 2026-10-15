package com.example.ecogiro.service;

import com.example.ecogiro.model.AppUser;
import com.example.ecogiro.model.Role;
import com.example.ecogiro.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AppUser register(String fullName, String cpf, Integer age, String email, String rawPassword, Role role) {
        String normalizedEmail = normalizeEmail(email);
        String normalizedCpf = cpf == null ? "" : cpf.replaceAll("\\D", "");

        if (fullName == null || fullName.isBlank()) throw new IllegalArgumentException("Nome completo é obrigatório.");
        if (normalizedCpf.length() != 11) throw new IllegalArgumentException("CPF deve conter 11 números.");
        if (age == null || age < 1 || age > 120) throw new IllegalArgumentException("Idade inválida.");
        if (normalizedEmail.isBlank() || !normalizedEmail.contains("@")) throw new IllegalArgumentException("E-mail inválido.");
        if (rawPassword == null || rawPassword.length() < 8) throw new IllegalArgumentException("A senha deve ter no mínimo 8 caracteres.");
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) throw new IllegalArgumentException("Este e-mail já está cadastrado.");
        if (userRepository.existsByCpf(normalizedCpf)) throw new IllegalArgumentException("Este CPF já está cadastrado.");

        AppUser user = new AppUser();
        user.setFullName(fullName.trim());
        user.setCpf(normalizedCpf);
        user.setAge(age);
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setActive(true);
        return userRepository.save(user);
    }

    public AppUser requireUser(String email) {
        return userRepository.findByEmailIgnoreCase(normalizeEmail(email))
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}
