package com.example.ecogiro.controller;
import com.example.ecogiro.model.*;
import com.example.ecogiro.repository.VehicleRepository;
import com.example.ecogiro.service.TerritoryAccessService;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
class InventoryControllerTest {
    @Test void inventarioIncluiInativosMasNaoVazaVeiculosForaDoEscopo() {
        var repository=mock(VehicleRepository.class); var access=mock(TerritoryAccessService.class);
        var auth=new UsernamePasswordAuthenticationToken("sector", "fixture");
        var actor=new AppUser();actor.setRole(Role.ADMIN_SETORIAL);
        var ownStore=new EcoGiroStore(); var otherStore=new EcoGiroStore();
        var own=new Vehicle();own.setCode("OWN");own.setType(VehicleType.BIKE);own.setStatus(VehicleStatus.INACTIVE);own.setStore(ownStore);
        var outside=new Vehicle();outside.setType(VehicleType.MOTO);outside.setStatus(VehicleStatus.AVAILABLE);outside.setStore(otherStore);
        when(access.actor(auth)).thenReturn(actor);when(access.permits(actor,ownStore)).thenReturn(true);
        when(repository.findAllByOrderByLocationNameAscCodeAsc()).thenReturn(List.of(own,outside));
        var result=new InventoryController(repository,access).inventory(auth);
        assertEquals(1,result.size());assertEquals("OWN",result.get(0).code());assertEquals("INACTIVE",result.get(0).status());
    }
    @Test void usuarioComumNaoConsultaInventarioMesmoChamandoController() {
        var repository=mock(VehicleRepository.class);var access=mock(TerritoryAccessService.class);
        var actor=new AppUser();actor.setRole(Role.USER);
        when(access.actor(null)).thenReturn(actor);
        assertThrows(org.springframework.web.server.ResponseStatusException.class,()->new InventoryController(repository,access).inventory(null));
        verifyNoInteractions(repository);
    }
}
