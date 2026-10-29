package com.example.ecogiro.controller;

import com.example.ecogiro.model.*;
import com.example.ecogiro.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Simulacao somente leitura. Nenhuma atribuicao e alterada sem aprovacao futura.
 * A heuristica atual considera quantidade de setores; carga de frota/lojas e etapa posterior.
 */
@RestController
@RequestMapping("/api/admin/sector-distribution")
public class SectorDistributionPreviewController {
    private final AppUserRepository users;
    private final AdministrativeSectorRepository sectors;
    private final AdministratorSectorAssignmentRepository assignments;

    public SectorDistributionPreviewController(AppUserRepository users,
                                               AdministrativeSectorRepository sectors,
                                               AdministratorSectorAssignmentRepository assignments) {
        this.users = users;
        this.sectors = sectors;
        this.assignments = assignments;
    }

    public record SuggestedTransfer(Long sectorId, String sectorName,
                                    Long fromAdministratorId, Long toAdministratorId,
                                    String reason) {}
    public record DistributionPreview(int activeAdministrators, int activeSectors,
                                      List<SuggestedTransfer> suggestions,
                                      String calculationBasis) {}

    @GetMapping("/preview")
    @Transactional(readOnly = true)
    public DistributionPreview preview(Authentication authentication) {
        AppUser actor = users.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        if (!actor.isActive() || actor.getRole() != Role.ADMIN_GERAL) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        List<AppUser> admins = users.findAll().stream()
                .filter(AppUser::isActive)
                .filter(u -> u.getRole() == Role.ADMIN_SETORIAL)
                .sorted(Comparator.comparing(AppUser::getId))
                .toList();
        List<AdministrativeSector> activeSectors = sectors.findAll().stream()
                .filter(AdministrativeSector::isActive)
                .sorted(Comparator.comparing(AdministrativeSector::getId))
                .toList();

        Map<Long, List<AdministrativeSector>> ownership = new HashMap<>();
        for (AppUser admin : admins) ownership.put(admin.getId(), new ArrayList<>());
        Set<Long> assigned = new HashSet<>();
        for (AdministratorSectorAssignment link : assignments.findAll()) {
            if (!link.getSector().isActive()) continue;
            assigned.add(link.getSector().getId());
            List<AdministrativeSector> owned = ownership.get(link.getAdministrator().getId());
            if (owned != null) owned.add(link.getSector());
        }

        List<SuggestedTransfer> proposed = new ArrayList<>();
        if (admins.isEmpty()) {
            return new DistributionPreview(0, activeSectors.size(), List.of(),
                    "Sem administradores setoriais ativos; nenhuma alteracao realizada.");
        }
        // Primeiro distribui setores sem responsavel na simulacao.
        for (AdministrativeSector sector : activeSectors) {
            if (assigned.contains(sector.getId())) continue;
            AppUser target = admins.stream()
                    .min(Comparator.comparingInt((AppUser a) -> ownership.get(a.getId()).size())
                            .thenComparing(AppUser::getId)).orElseThrow();
            proposed.add(new SuggestedTransfer(sector.getId(), sector.getName(),
                    null, target.getId(), "Setor sem responsavel"));
            ownership.get(target.getId()).add(sector);
        }

        // Depois sugere transferencias para equilibrar numero de setores.
        // Nao modifica a base; a lista e apenas uma proposta a ser revisada.
        while (true) {
            AppUser most = admins.stream()
                    .max(Comparator.comparingInt((AppUser a) -> ownership.get(a.getId()).size())
                            .thenComparing(a -> -a.getId())).orElseThrow();
            AppUser least = admins.stream()
                    .min(Comparator.comparingInt((AppUser a) -> ownership.get(a.getId()).size())
                            .thenComparing(AppUser::getId)).orElseThrow();
            if (ownership.get(most.getId()).size() - ownership.get(least.getId()).size() <= 1) break;
            List<AdministrativeSector> from = ownership.get(most.getId());
            AdministrativeSector sector = from.remove(from.size() - 1);
            ownership.get(least.getId()).add(sector);
            proposed.add(new SuggestedTransfer(sector.getId(), sector.getName(),
                    most.getId(), least.getId(), "Equilibrio preliminar por quantidade de setores"));
        }
        return new DistributionPreview(admins.size(), activeSectors.size(),
                List.copyOf(proposed), "Simulacao por numero de setores; sem aprovacao ou alteracao de dados.");
    }
}
