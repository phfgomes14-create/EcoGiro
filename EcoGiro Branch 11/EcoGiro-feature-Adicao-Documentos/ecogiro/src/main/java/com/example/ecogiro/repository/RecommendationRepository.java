package com.example.ecogiro.repository;

import com.example.ecogiro.model.AppUser;
import com.example.ecogiro.model.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findTop10ByUserOrderByCreatedAtDesc(AppUser user);
}
