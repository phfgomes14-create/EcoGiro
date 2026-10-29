package com.example.ecogiro.service;

import com.example.ecogiro.model.*;
import com.example.ecogiro.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SectorRedistributionServiceTest {
    private AppUserRepository users;
    private AdministrativeSectorRepository sectors;
    private AdministratorSectorAssignmentRepository assignments;
    private SectorRedistributionProposalRepository proposals;
    private SectorRedistributionService service;
    private Authentication auth;

    @BeforeEach
    void setUp() {
        users = mock(AppUserRepository.class);
        sectors = mock(AdministrativeSectorRepository.class);
        assignments = mock(AdministratorSectorAssignmentRepository.class);
        proposals = mock(SectorRedistributionProposalRepository.class);
        auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("admin@ecogiro.test");
        service = new SectorRedistributionService(users, sectors, assignments, proposals);
    }

    @Test
    void usuarioComumNaoPodeCriarProposta() {
        AppUser actor = new AppUser();
        actor.setRole(Role.USER);
        actor.setActive(true);
        when(users.findByEmailIgnoreCase("admin@ecogiro.test")).thenReturn(Optional.of(actor));

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.create(auth, 1L, 2L, "Equilibrar"));
        assertEquals(HttpStatus.FORBIDDEN, error.getStatusCode());
        verifyNoInteractions(sectors, assignments, proposals);
    }

    @Test
    void adminLegadoNaoPodeAprovarProposta() {
        AppUser actor = new AppUser();
        actor.setRole(Role.ADMIN);
        actor.setActive(true);
        when(users.findByEmailIgnoreCase("admin@ecogiro.test")).thenReturn(Optional.of(actor));

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.decide(auth, 10L, true));
        assertEquals(HttpStatus.FORBIDDEN, error.getStatusCode());
        verifyNoInteractions(sectors, assignments, proposals);
    }

    @Test
    void administradorGeralNaoPodeAprovarDuasVezes() {
        AppUser actor = new AppUser();
        actor.setRole(Role.ADMIN_GERAL);
        actor.setActive(true);
        when(users.findByEmailIgnoreCase("admin@ecogiro.test")).thenReturn(Optional.of(actor));
        SectorRedistributionProposal proposal = new SectorRedistributionProposal();
        proposal.setStatus(SectorRedistributionProposal.Status.APPROVED);
        when(proposals.lockById(10L)).thenReturn(Optional.of(proposal));

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.decide(auth, 10L, true));
        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
        verifyNoInteractions(sectors, assignments);
    }
}
