package com.example.ecogiro.repository;

import com.example.ecogiro.model.AppUser;
import com.example.ecogiro.model.SubscriptionStatus;
import com.example.ecogiro.model.UserPlanSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserPlanSubscriptionRepository extends JpaRepository<UserPlanSubscription, Long> {
    List<UserPlanSubscription> findByUserOrderByCreatedAtDesc(AppUser user);
    Optional<UserPlanSubscription> findFirstByUserAndStatusOrderByCreatedAtDesc(AppUser user, SubscriptionStatus status);
    boolean existsByRequestId(Long requestId);
}
