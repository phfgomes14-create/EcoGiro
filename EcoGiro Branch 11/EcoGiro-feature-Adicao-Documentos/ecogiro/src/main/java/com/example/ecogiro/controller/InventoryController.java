package com.example.ecogiro.controller;

import com.example.ecogiro.model.Vehicle;
import com.example.ecogiro.repository.VehicleRepository;
import com.example.ecogiro.service.TerritoryAccessService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

/** Estoque completo, incluindo inativos, limitado ao território autorizado no servidor. */
@RestController
public class InventoryController {
    private final VehicleRepository vehicles;
    private final TerritoryAccessService access;
    public InventoryController(VehicleRepository vehicles, TerritoryAccessService access) {
        this.vehicles = vehicles;
        this.access = access;
    }
    @GetMapping("/api/admin/operations/inventory")
    @Transactional(readOnly = true)
    public List<InventoryView> inventory(Authentication authentication) {
        var actor = access.actor(authentication);
        if (!actor.getRole().isAdministrator()) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return vehicles.findAllByOrderByLocationNameAscCodeAsc().stream()
            .filter(vehicle -> access.permits(actor, vehicle.getStore()))
            .map(InventoryView::from).toList();
    }
    public record InventoryView(Long id, String code, String model, String type, String status,
                                String locationName, Double latitude, Double longitude, Long storeId) {
        static InventoryView from(Vehicle vehicle) {
            return new InventoryView(vehicle.getId(), vehicle.getCode(), vehicle.getModel(),
                vehicle.getType().name(), vehicle.getStatus().name(), vehicle.getLocationName(),
                vehicle.getLatitude(), vehicle.getLongitude(), vehicle.getStore() == null ? null : vehicle.getStore().getId());
        }
    }
}
