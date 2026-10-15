package com.example.ecogiro.service;

import com.example.ecogiro.model.*;
import com.example.ecogiro.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SectorRedistributionService {
    private final AppUserRepository users;
    private final AdministrativeSectorRepository sectors;
    private final AdministratorSectorAssignmentRepository assignments;
    private final SectorRedistributionProposalRepository proposals;

    public SectorRedistributionService(AppUserRepository users,
            AdministrativeSectorRepository sectors,
            AdministratorSectorAssignmentRepository assignments,
            SectorRedistributionProposalRepository proposals) {
        this.users = users;
        this.sectors = sectors;
        this.assignments = assignments;
        this.proposals = proposals;
    }

    private AppUser requireGeneral(Authentication authentication) {
        if (authentication == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        AppUser actor = users.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        if (!actor.isActive() || actor.getRole() != Role.ADMIN_GERAL)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return actor;
    }

    @Transactional
    public Long create(Authentication authentication, Long sectorId, Long destinationId, String reason) {
        AppUser actor = requireGeneral(authentication);
        if (sectorId == null || destinationId == null || reason == null || reason.isBlank()
                || reason.length() > 500)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados da proposta invalidos.");
        AdministrativeSector sector = sectors.lockById(sectorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Setor nao encontrado."));
        if (!sector.isActive())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Setor inativo.");
        AppUser target = users.findById(destinationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Destino nao encontrado."));
        if (!target.isActive() || target.getRole() != Role.ADMIN_SETORIAL)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Destino deve ser administrador setorial ativo.");

        List<AdministratorSectorAssignment> current = assignments.findBySectorId(sectorId);
        if (current.size() > 1)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Setor com multiplos responsaveis; exige revisao.");
        AppUser origin = current.isEmpty() ? null : current.get(0).getAdministrator();
        if (origin != null && origin.getId().equals(destinationId))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Setor ja pertence ao destino.");
        if (proposals.existsBySectorIdAndStatus(sectorId, SectorRedistributionProposal.Status.PENDING))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ja existe proposta pendente para o setor.");

        SectorRedistributionProposal p = new SectorRedistributionProposal();
        p.setSector(sector);
        p.setFromAdministrator(origin);
        p.setToAdministrator(target);
        p.setRequestedBy(actor);
        p.setReason(reason.trim());
        return proposals.save(p).getId();
    }

    @Transactional
    public void decide(Authentication authentication, Long proposalId, boolean approve) {
        AppUser actor = requireGeneral(authentication);
        SectorRedistributionProposal p = proposals.lockById(proposalId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proposta nao encontrada."));
        if (p.getStatus() != SectorRedistributionProposal.Status.PENDING)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Proposta ja decidida.");
        // Cada setor e bloqueado antes de alterar a atribuicao.
        AdministrativeSector sector = sectors.lockById(p.getSector().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Setor removido."));
        if (!sector.isActive())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Setor inativo.");

        if (approve) {
            AppUser target = users.findById(p.getToAdministrator().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Destino removido."));
            if (!target.isActive() || target.getRole() != Role.ADMIN_SETORIAL)
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Destino nao e administrador setorial ativo.");
            List<AdministratorSectorAssignment> current = assignments.findBySectorId(sector.getId());
            if (current.size() > 1)
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Atribuicao ambigua.");
            Long currentId = current.isEmpty() ? null : current.get(0).getAdministrator().getId();
            Long expectedId = p.getFromAdministrator() == null ? null : p.getFromAdministrator().getId();
            if (!java.util.Objects.equals(currentId, expectedId))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Atribuicao mudou desde a proposta.");
            assignments.deleteBySectorId(sector.getId());
            assignments.flush();
            AdministratorSectorAssignment next = new AdministratorSectorAssignment();
            next.setSector(sector);
            next.setAdministrator(target);
            assignments.save(next);
        }
        p.setStatus(approve ? SectorRedistributionProposal.Status.APPROVED
                            : SectorRedistributionProposal.Status.REJECTED);
        p.setDecidedBy(actor);
        p.setDecidedAt(LocalDateTime.now());
    }
}
