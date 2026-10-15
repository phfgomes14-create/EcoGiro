package com.example.ecogiro.controller;

import com.example.ecogiro.model.Vehicle;
import com.example.ecogiro.model.VehicleStatus;
import com.example.ecogiro.repository.VehicleRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/map")
public class MapApiController {
    private final VehicleRepository vehicleRepository;
    private final com.example.ecogiro.service.TerritoryAccessService access;

    public MapApiController(VehicleRepository vehicleRepository, com.example.ecogiro.service.TerritoryAccessService access) {
        this.vehicleRepository = vehicleRepository; this.access=access;
    }

    @GetMapping("/vehicles")
    @org.springframework.transaction.annotation.Transactional(readOnly=true)
    public List<VehicleMapView> vehicles(org.springframework.security.core.Authentication auth) {
        var actor=access.actor(auth);
        return vehicleRepository.findAllByOrderByLocationNameAscCodeAsc().stream()
                .filter(v -> actor.getRole()!=com.example.ecogiro.model.Role.ADMIN_SETORIAL || access.permits(actor,v.getStore()))
                .filter(v -> v.getStatus() != VehicleStatus.INACTIVE)
                .map(VehicleMapView::from)
                .toList();
    }

    public record VehicleMapView(Long id, String code, String model, String type, String status,
                                 Double latitude, Double longitude, String locationName, LocalDateTime updatedAt) {
        static VehicleMapView from(Vehicle vehicle) {
            return new VehicleMapView(vehicle.getId(), vehicle.getCode(), vehicle.getModel(), vehicle.getType().name(),
                    vehicle.getStatus().name(), vehicle.getLatitude(), vehicle.getLongitude(), vehicle.getLocationName(), vehicle.getUpdatedAt());
        }
    }
}
