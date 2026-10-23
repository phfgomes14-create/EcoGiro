package com.example.ecogiro.repository;

import com.example.ecogiro.model.AppUser;
import com.example.ecogiro.model.PlanRequest;
import com.example.ecogiro.model.PlanRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanRequestRepository extends JpaRepository<PlanRequest, Long> {
    List<PlanRequest> findByUserOrderByRequestedAtDesc(AppUser user);
    List<PlanRequest> findAllByOrderByRequestedAtDesc();
    boolean existsByUserAndStatus(AppUser user, PlanRequestStatus status);
    boolean existsByUserAndPlanIdAndStatus(AppUser user, Long planId, PlanRequestStatus status);
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select r from PlanRequest r where r.id = :id")
    java.util.Optional<PlanRequest> lockById(@org.springframework.data.repository.query.Param("id") Long id);
    long countByStatus(PlanRequestStatus status);
}
