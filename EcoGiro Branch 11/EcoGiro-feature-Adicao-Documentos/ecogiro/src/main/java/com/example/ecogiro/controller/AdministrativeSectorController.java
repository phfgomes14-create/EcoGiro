package com.example.ecogiro.controller;

import com.example.ecogiro.model.AdministratorSectorAssignment;
import com.example.ecogiro.model.AppUser;
import com.example.ecogiro.model.Role;
import com.example.ecogiro.repository.AdministratorSectorAssignmentRepository;
import com.example.ecogiro.repository.AdministrativeSectorRepository;
import com.example.ecogiro.repository.AppUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/admin/sectors")
public class AdministrativeSectorController {
    private final AppUserRepository users;
    private final AdministrativeSectorRepository sectors;
    private final AdministratorSectorAssignmentRepository assignments;

    public AdministrativeSectorController(AppUserRepository users,
                                          AdministrativeSectorRepository sectors,
                                          AdministratorSectorAssignmentRepository assignments) {
        this.users = users;
        this.sectors = sectors;
        this.assignments = assignments;
    }

    public record SectorView(Long id, String code, String name, boolean active) {}

    @GetMapping
    @Transactional(readOnly = true)
    public List<SectorView> mySectors(Authentication authentication) {
        AppUser actor = users.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        Role role = actor.getRole();
        if (!actor.isActive() || !role.isAdministrator()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if (role == Role.ADMIN_GERAL) {
            return sectors.findAll().stream()
                    .map(s -> new SectorView(s.getId(), s.getCode(), s.getName(), s.isActive()))
                    .toList();
        }
        // ADMIN legado nao recebe automaticamente privilegio territorial.
        if (role == Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Atribuicao territorial do administrador legado pendente.");
        }
        return assignments.findByAdministratorId(actor.getId()).stream()
                .map(AdministratorSectorAssignment::getSector)
                .filter(s -> s.isActive())
                .map(s -> new SectorView(s.getId(), s.getCode(), s.getName(), s.isActive()))
                .distinct()
                .toList();
    }
}
