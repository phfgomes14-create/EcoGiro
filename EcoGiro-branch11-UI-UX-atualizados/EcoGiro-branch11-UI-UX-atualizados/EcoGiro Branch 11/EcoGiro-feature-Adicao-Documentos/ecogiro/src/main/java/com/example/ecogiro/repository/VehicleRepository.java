package com.example.ecogiro.repository;

import com.example.ecogiro.model.Vehicle;
import com.example.ecogiro.model.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findAllByOrderByLocationNameAscCodeAsc();
    List<Vehicle> findByStatusOrderByLocationNameAsc(VehicleStatus status);
    boolean existsByCodeIgnoreCase(String code);
    Optional<Vehicle> findByCodeIgnoreCase(String code);
}
