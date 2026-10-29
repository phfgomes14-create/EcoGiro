package com.example.ecogiro.repository;

import com.example.ecogiro.model.EcoGiroStore;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EcoGiroStoreRepository extends JpaRepository<EcoGiroStore, Long> {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select s from EcoGiroStore s where s.id = :id")
    java.util.Optional<EcoGiroStore> lockById(@org.springframework.data.repository.query.Param("id") Long id);
    List<EcoGiroStore> findBySectorId(Long sectorId);
}
