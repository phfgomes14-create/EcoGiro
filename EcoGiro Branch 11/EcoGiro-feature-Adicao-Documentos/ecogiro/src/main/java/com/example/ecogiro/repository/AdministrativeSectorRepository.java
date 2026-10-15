package com.example.ecogiro.repository;

import com.example.ecogiro.model.AdministrativeSector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface AdministrativeSectorRepository extends JpaRepository<AdministrativeSector, Long> {
    boolean existsByCode(String code);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from AdministrativeSector s where s.id = :id")
    Optional<AdministrativeSector> lockById(@Param("id") Long id);
}
