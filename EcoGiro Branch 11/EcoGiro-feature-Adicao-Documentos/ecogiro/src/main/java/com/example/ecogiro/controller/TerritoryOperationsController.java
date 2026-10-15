package com.example.ecogiro.controller;
import com.example.ecogiro.model.*;
import com.example.ecogiro.repository.*;
import com.example.ecogiro.service.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/admin/operations")
public class TerritoryOperationsController {
    private final TerritoryAccessService access;
    private final TerritoryFleetService fleet;
    private final EcoGiroStoreRepository stores;
    private final AdministrativeSectorRepository sectors;
    private final VehicleRepository vehicles;
    public TerritoryOperationsController(TerritoryAccessService access, TerritoryFleetService fleet,
        EcoGiroStoreRepository stores, AdministrativeSectorRepository sectors, VehicleRepository vehicles) {
        this.access=access; this.fleet=fleet; this.stores=stores; this.sectors=sectors; this.vehicles=vehicles;
    }
    public record StoreView(Long id,String name,Long sectorId,String sectorName,Double latitude,Double longitude,int capacity,long occupancy,boolean blocked,boolean active) {}
    public record FleetView(Long id,String code,String model,String status,Long storeId) {}
    public record StoreRequest(@NotBlank @Size(max=40) String code,@NotBlank @Size(max=120) String name,@NotNull Long sectorId,
        @NotNull @DecimalMin("-90") @DecimalMax("90") Double latitude,@NotNull @DecimalMin("-180") @DecimalMax("180") Double longitude,
        @Min(1) @Max(10000) int capacity) {}
    public record StoreLimits(@Min(1) @Max(10000) int capacity,boolean blocked) {}
    public record SectorRequest(@NotBlank @Size(max=40) String code,@NotBlank @Size(max=120) String name) {}
    public record MoveRequest(@NotNull Long storeId) {}
    public record StatusRequest(@NotNull VehicleStatus status) {}
    @GetMapping("/csrf")
    public SectorRedistributionQueueController.CsrfResponse csrf(Authentication auth,CsrfToken token) {
        AppUser actor=access.actor(auth); if(!actor.getRole().isAdministrator()) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return new SectorRedistributionQueueController.CsrfResponse(token.getHeaderName(),token.getToken());
    }
    @GetMapping("/stores") @Transactional(readOnly=true)
    public List<StoreView> stores(Authentication auth) {
        AppUser actor=access.actor(auth); if(!actor.getRole().isAdministrator()) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return stores.findAll().stream().filter(s -> access.permits(actor,s)).map(s -> new StoreView(s.getId(),s.getName(),s.getSector().getId(),s.getSector().getName(),s.getLatitude(),s.getLongitude(),s.getCapacity(),vehicles.countByStoreId(s.getId()),s.isBlocked(),s.isActive())).toList();
    }
    @GetMapping("/vehicles") @Transactional(readOnly=true)
    public List<FleetView> vehicles(Authentication auth) {
        AppUser actor=access.actor(auth); if(!actor.getRole().isAdministrator()) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return vehicles.findAll().stream().filter(v -> access.permits(actor,v.getStore())).map(v -> new FleetView(v.getId(),v.getCode(),v.getModel(),v.getStatus().name(),v.getStore()==null?null:v.getStore().getId())).toList();
    }
    @PostMapping("/sectors")
    public void createSector(Authentication auth,@Valid @RequestBody SectorRequest body) {
        requireGeneral(auth); AdministrativeSector s=new AdministrativeSector();s.setCode(body.code().trim());s.setName(body.name().trim());sectors.save(s);
    }
    @PostMapping("/stores")
    public void createStore(Authentication auth,@Valid @RequestBody StoreRequest body) {
        requireGeneral(auth); AdministrativeSector sector=sectors.findById(body.sectorId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if(!sector.isActive()) throw new ResponseStatusException(HttpStatus.CONFLICT);
        EcoGiroStore s=new EcoGiroStore();s.setCode(body.code().trim());s.setName(body.name().trim());s.setSector(sector);s.setLatitude(body.latitude());s.setLongitude(body.longitude());s.setCapacity(body.capacity());stores.save(s);
    }
    @PostMapping("/stores/{id}/limits") @Transactional
    public void limits(Authentication auth,@PathVariable Long id,@Valid @RequestBody StoreLimits body) {
        AppUser actor=access.actor(auth);EcoGiroStore s=stores.lockById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));access.require(actor,s);
        if(body.capacity()<vehicles.countByStoreId(id)) throw new ResponseStatusException(HttpStatus.CONFLICT,"Capacidade menor que a frota atual.");
        s.setCapacity(body.capacity());s.setBlocked(body.blocked());stores.save(s);
    }
    @PostMapping("/vehicles/{id}/move")
    public void move(Authentication auth,@PathVariable Long id,@Valid @RequestBody MoveRequest body) { fleet.move(auth,id,body.storeId()); }
    @PostMapping("/vehicles/{id}/status")
    public void status(Authentication auth,@PathVariable Long id,@Valid @RequestBody StatusRequest body) { fleet.status(auth,id,body.status()); }
    private void requireGeneral(Authentication auth) { if(access.actor(auth).getRole()!=Role.ADMIN_GERAL) throw new ResponseStatusException(HttpStatus.FORBIDDEN); }
}
