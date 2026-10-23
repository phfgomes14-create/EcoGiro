package com.example.ecogiro.service;
import com.example.ecogiro.model.*;
import com.example.ecogiro.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TerritoryAccessService {
    private final AppUserRepository users;
    private final AdministratorSectorAssignmentRepository assignments;
    public TerritoryAccessService(AppUserRepository users, AdministratorSectorAssignmentRepository assignments) {
        this.users = users; this.assignments = assignments;
    }
    public AppUser actor(Authentication authentication) {
        if (authentication == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        AppUser user = users.findByEmailIgnoreCase(authentication.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        if (!user.isActive()) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return user;
    }
    public Set<Long> sectorIds(AppUser user) {
        return assignments.findByAdministratorId(user.getId()).stream()
            .map(AdministratorSectorAssignment::getSector).filter(AdministrativeSector::isActive)
            .map(AdministrativeSector::getId).collect(Collectors.toSet());
    }
    public boolean permits(AppUser actor, EcoGiroStore store) {
        if (actor.getRole() == Role.ADMIN || actor.getRole() == Role.ADMIN_GERAL) return true;
        return actor.getRole() == Role.ADMIN_SETORIAL && store != null && store.isActive()
            && store.getSector().isActive() && sectorIds(actor).contains(store.getSector().getId());
    }
    public void require(AppUser actor, EcoGiroStore store) {
        if (!actor.getRole().isAdministrator() || !permits(actor, store))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Operação fora dos setores autorizados.");
    }
}
