package com.example.ecogiro.service;
import com.example.ecogiro.model.*;
import com.example.ecogiro.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class TerritoryAccessServiceTest {
    @Test void setorialNaoAcessaOutroSetorOuVeiculoSemLoja() {
        var assignments=mock(AdministratorSectorAssignmentRepository.class);
        var access=new TerritoryAccessService(mock(AppUserRepository.class),assignments);
        var actor=new AppUser();actor.setRole(Role.ADMIN_SETORIAL);ReflectionTestUtils.setField(actor,"id",1L);
        var own=new AdministrativeSector();ReflectionTestUtils.setField(own,"id",10L);
        var other=new AdministrativeSector();ReflectionTestUtils.setField(other,"id",20L);
        var link=new AdministratorSectorAssignment();link.setSector(own);link.setAdministrator(actor);
        when(assignments.findByAdministratorId(1L)).thenReturn(List.of(link));
        var store=new EcoGiroStore();store.setSector(own);assertTrue(access.permits(actor,store));
        store.setSector(other);assertFalse(access.permits(actor,store));assertFalse(access.permits(actor,null));
        store.setSector(own);own.setActive(false);assertFalse(access.permits(actor,store));
    }
    @Test void usuarioNaoOperaFrotaEGeralPodeAtribuirSemLoja() {
        var access=new TerritoryAccessService(mock(AppUserRepository.class),mock(AdministratorSectorAssignmentRepository.class));
        var actor=new AppUser();actor.setRole(Role.USER);assertFalse(access.permits(actor,null));
        actor.setRole(Role.ADMIN_GERAL);assertTrue(access.permits(actor,null));
    }
}
