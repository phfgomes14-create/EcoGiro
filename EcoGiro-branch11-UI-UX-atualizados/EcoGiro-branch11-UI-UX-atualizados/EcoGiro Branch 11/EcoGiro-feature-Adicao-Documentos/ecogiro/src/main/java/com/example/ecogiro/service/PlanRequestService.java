package com.example.ecogiro.service;

import com.example.ecogiro.model.*;
import com.example.ecogiro.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class PlanRequestService {
    private final PlanRequestRepository requestRepository;
    private final RentalPlanRepository planRepository;
    private final UserPlanSubscriptionRepository subscriptionRepository;

    public PlanRequestService(PlanRequestRepository requestRepository,
                              RentalPlanRepository planRepository,
                              UserPlanSubscriptionRepository subscriptionRepository) {
        this.requestRepository = requestRepository;
        this.planRepository = planRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Transactional
    public PlanRequest requestPlan(AppUser user, Long planId, String message) {
        RentalPlan plan = planRepository.findById(planId)
                .filter(RentalPlan::isActive)
                .orElseThrow(() -> new IllegalArgumentException("Plano indisponível."));

        if (requestRepository.existsByUserAndPlanIdAndStatus(user, planId, PlanRequestStatus.PENDING)) {
            throw new IllegalStateException("Você já possui uma solicitação pendente para este plano.");
        }

        PlanRequest request = new PlanRequest();
        request.setUser(user);
        request.setPlan(plan);
        request.setUserMessage(trimTo(message, 500));
        return requestRepository.save(request);
    }

    @Transactional
    public PlanRequest respond(Long requestId, PlanRequestStatus newStatus, String response, AppUser admin) {
        if (newStatus != PlanRequestStatus.APPROVED && newStatus != PlanRequestStatus.REJECTED) {
            throw new IllegalArgumentException("A resposta deve ser APPROVED ou REJECTED.");
        }

        PlanRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada."));
        if (request.getStatus() != PlanRequestStatus.PENDING) {
            throw new IllegalStateException("Esta solicitação já foi respondida.");
        }

        request.setStatus(newStatus);
        request.setAdminResponse(trimTo(response, 500));
        request.setRespondedBy(admin);
        request.setRespondedAt(LocalDateTime.now());
        requestRepository.save(request);

        if (newStatus == PlanRequestStatus.APPROVED && !subscriptionRepository.existsByRequestId(request.getId())) {
            subscriptionRepository.findFirstByUserAndStatusOrderByCreatedAtDesc(request.getUser(), SubscriptionStatus.ACTIVE)
                    .ifPresent(old -> old.setStatus(SubscriptionStatus.CANCELLED));

            UserPlanSubscription subscription = new UserPlanSubscription();
            subscription.setUser(request.getUser());
            subscription.setPlan(request.getPlan());
            subscription.setRequest(request);
            subscription.setStatus(SubscriptionStatus.ACTIVE);
            subscription.setStartDate(LocalDate.now());
            subscription.setEndDate(LocalDate.now().plusDays(request.getPlan().getDurationDays()));
            subscriptionRepository.save(subscription);
        }
        return request;
    }

    @Transactional
    public PlanRequest cancel(Long requestId, AppUser user) {
        PlanRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada."));
        if (!request.getUser().getId().equals(user.getId())) throw new IllegalArgumentException("Solicitação inválida.");
        if (request.getStatus() != PlanRequestStatus.PENDING) throw new IllegalStateException("Apenas solicitações pendentes podem ser canceladas.");
        request.setStatus(PlanRequestStatus.CANCELLED);
        return requestRepository.save(request);
    }

    private String trimTo(String value, int max) {
        if (value == null) return null;
        String text = value.trim();
        return text.length() <= max ? text : text.substring(0, max);
    }
}
