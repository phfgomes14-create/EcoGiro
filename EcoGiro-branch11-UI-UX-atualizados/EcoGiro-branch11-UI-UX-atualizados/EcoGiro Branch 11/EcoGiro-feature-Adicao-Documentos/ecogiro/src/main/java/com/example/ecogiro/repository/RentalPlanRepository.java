package com.example.ecogiro.repository;

import com.example.ecogiro.model.RentalPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalPlanRepository extends JpaRepository<RentalPlan, Long> {
    List<RentalPlan> findByActiveTrueOrderByPriceAsc();
    List<RentalPlan> findAllByOrderByCreatedAtDesc();
}
