package com.example.ecogiro.repository;

import com.example.ecogiro.model.AppUser;
import com.example.ecogiro.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByCpf(String cpf);
    List<AppUser> findAllByOrderByCreatedAtDesc();
    long countByRole(Role role);
}
