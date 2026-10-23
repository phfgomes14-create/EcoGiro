package com.example.ecogiro.repository;

import com.example.ecogiro.model.Vehicle;
import com.example.ecogiro.model.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    long countByStoreId(Long storeId);
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select v from Vehicle v where v.id = :id")
    Optional<Vehicle> lockById(@org.springframework.data.repository.query.Param("id") Long id);
    List<Vehicle> findAllByOrderByLocationNameAscCodeAsc();
    List<Vehicle> findByStatusOrderByLocationNameAsc(VehicleStatus status);
    boolean existsByCodeIgnoreCase(String code);
    Optional<Vehicle> findByCodeIgnoreCase(String code);
}
