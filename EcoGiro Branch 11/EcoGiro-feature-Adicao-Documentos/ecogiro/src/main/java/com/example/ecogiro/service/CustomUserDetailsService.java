package com.example.ecogiro.service;

import com.example.ecogiro.model.AppUser;
import com.example.ecogiro.repository.AppUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final AppUserRepository userRepository;

    public CustomUserDetailsService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepository.findByEmailIgnoreCase(username.trim())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        return User.withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRole() == com.example.ecogiro.model.Role.ADMIN
                        ? new String[]{"ROLE_ADMIN", "ROLE_ADMIN_LEGACY"}
                        : user.getRole().isAdministrator()
                            ? new String[]{"ROLE_ADMIN", "ROLE_" + user.getRole().name()}
                            : new String[]{"ROLE_USER"})
                .disabled(!user.isActive())
                .build();
    }
}
