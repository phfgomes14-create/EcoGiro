package com.example.ecogiro.service;
import com.example.ecogiro.model.*;
import com.example.ecogiro.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class TerritoryFleetServiceTest {
    TerritoryAccessService access; VehicleRepository vehicles; EcoGiroStoreRepository stores;
    TerritoryFleetService service; Authentication auth; AppUser actor; EcoGiroStore target; Vehicle vehicle;
    @BeforeEach void setup() {
        access=mock(TerritoryAccessService.class);vehicles=mock(VehicleRepository.class);stores=mock(EcoGiroStoreRepository.class);
        auth=mock(Authentication.class);actor=new AppUser();target=new EcoGiroStore();vehicle=new Vehicle();
        AdministrativeSector sector=new AdministrativeSector();target.setSector(sector);target.setCapacity(2);
        target.setName("Centro");target.setLatitude(-23.0);target.setLongitude(-46.0);
        ReflectionTestUtils.setField(target,"id",1L);vehicle.setStatus(VehicleStatus.AVAILABLE);
        when(access.actor(auth)).thenReturn(actor);when(stores.lockById(1L)).thenReturn(Optional.of(target));
        when(vehicles.lockById(2L)).thenReturn(Optional.of(vehicle));service=new TerritoryFleetService(access,vehicles,stores);
    }
    @Test void lojaLotadaNaoRecebeVeiculo() {
        when(vehicles.countByStoreId(1L)).thenReturn(2L);
        assertThrows(ResponseStatusException.class,()->service.move(auth,2L,1L));verify(vehicles,never()).save(any());
    }
    @Test void destinoBloqueadoNaoRecebeVeiculo() {
        target.setBlocked(true);assertThrows(ResponseStatusException.class,()->service.move(auth,2L,1L));verify(vehicles,never()).save(any());
    }
    @Test void alugadoNaoPodeSerTransferidoOuAlterado() {
        vehicle.setStatus(VehicleStatus.RENTED);
        assertThrows(ResponseStatusException.class,()->service.move(auth,2L,1L));
        assertThrows(ResponseStatusException.class,()->service.status(auth,2L,VehicleStatus.AVAILABLE));verify(vehicles,never()).save(any());
    }
    @Test void transferenciaAtualizaLojaECoordenadas() {
        service.move(auth,2L,1L);assertSame(target,vehicle.getStore());assertEquals(-23.0,vehicle.getLatitude());assertEquals("Centro",vehicle.getLocationName());verify(vehicles).save(vehicle);
    }
    @Test void mesmaLojaLotadaNaoContaVeiculoDuasVezes() {
        vehicle.setStore(target);when(vehicles.countByStoreId(1L)).thenReturn(2L);service.move(auth,2L,1L);verify(vehicles).save(vehicle);
    }
    @Test void acessoOrigemRecusadoImpedeTransferencia() {
        EcoGiroStore source=new EcoGiroStore();vehicle.setStore(source);
        doThrow(new ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN)).when(access).require(actor,source);
        assertThrows(ResponseStatusException.class,()->service.move(auth,2L,1L));verify(vehicles,never()).save(any());
    }
}
