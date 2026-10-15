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

    public MapApiController(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @GetMapping("/vehicles")
    public List<VehicleMapView> vehicles() {
        return vehicleRepository.findAllByOrderByLocationNameAscCodeAsc().stream()
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
