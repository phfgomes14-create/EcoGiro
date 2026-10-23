package com.example.ecogiro.repository;

import com.example.ecogiro.model.AdministratorSectorAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AdministratorSectorAssignmentRepository extends JpaRepository<AdministratorSectorAssignment, Long> {
    List<AdministratorSectorAssignment> findByAdministratorId(Long administratorId);
    List<AdministratorSectorAssignment> findBySectorId(Long sectorId);
    void deleteBySectorId(Long sectorId);
}
