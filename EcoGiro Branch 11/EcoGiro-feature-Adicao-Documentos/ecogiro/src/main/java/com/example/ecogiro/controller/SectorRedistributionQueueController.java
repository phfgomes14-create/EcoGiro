package com.example.ecogiro.controller;

import com.example.ecogiro.model.AppUser;
import com.example.ecogiro.model.Role;
import com.example.ecogiro.model.SectorRedistributionProposal;
import com.example.ecogiro.repository.AppUserRepository;
import com.example.ecogiro.repository.SectorRedistributionProposalRepository;
import com.example.ecogiro.service.SectorRedistributionService;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/redistribution-proposals")
public class SectorRedistributionQueueController {
    private final AppUserRepository users;
    private final SectorRedistributionProposalRepository proposals;
    private final SectorRedistributionService distribution;

    public SectorRedistributionQueueController(
            AppUserRepository users, SectorRedistributionProposalRepository proposals,
            SectorRedistributionService distribution) {
        this.users = users;
        this.proposals = proposals;
        this.distribution = distribution;
    }

    public record ProposalView(Long id, Long sectorId, String sectorName,
                               Long fromAdministratorId, Long toAdministratorId,
                               Long requestedById, String reason, String status,
                               LocalDateTime createdAt) {}


    public record ProposalRequest(Long sectorId, Long toAdministratorId, String reason) {}
    public record ProposalCreated(Long proposalId) {}
    public record CsrfResponse(String headerName, String token) {}

    @GetMapping("/csrf")
    public CsrfResponse csrf(Authentication authentication, CsrfToken csrfToken) {
        requireGeneral(authentication);
        return new CsrfResponse(csrfToken.getHeaderName(), csrfToken.getToken());
    }

    @PostMapping
    public ProposalCreated create(Authentication authentication, @RequestBody ProposalRequest request) {
        requireGeneral(authentication);
        if (request == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        return new ProposalCreated(distribution.create(authentication,
                request.sectorId(), request.toAdministratorId(), request.reason()));
    }

    @PostMapping("/{id}/approve")
    public void approve(Authentication authentication, @PathVariable Long id) {
        requireGeneral(authentication);
        distribution.decide(authentication, id, true);
    }

    @PostMapping("/{id}/reject")
    public void reject(Authentication authentication, @PathVariable Long id) {
        requireGeneral(authentication);
        distribution.decide(authentication, id, false);
    }

    private void requireGeneral(Authentication authentication) {
        if (authentication == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        AppUser actor = users.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        if (!actor.isActive() || actor.getRole() != Role.ADMIN_GERAL)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    @GetMapping("/pending")
    @Transactional(readOnly = true)
    public List<ProposalView> pending(Authentication authentication) {
        if (authentication == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        AppUser actor = users.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        if (!actor.isActive() || actor.getRole() != Role.ADMIN_GERAL) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return proposals.findByStatusOrderByCreatedAtDesc(
                    SectorRedistributionProposal.Status.PENDING).stream()
                .map(p -> new ProposalView(p.getId(), p.getSector().getId(),
                        p.getSector().getName(),
                        p.getFromAdministrator() == null ? null : p.getFromAdministrator().getId(),
                        p.getToAdministrator().getId(), p.getRequestedBy().getId(),
                        p.getReason(), p.getStatus().name(), p.getCreatedAt()))
                .toList();
    }
}
