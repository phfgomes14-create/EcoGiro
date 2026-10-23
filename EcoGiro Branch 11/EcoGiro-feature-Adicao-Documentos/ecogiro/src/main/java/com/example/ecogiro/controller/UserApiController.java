package com.example.ecogiro.controller;

import com.example.ecogiro.model.*;
import com.example.ecogiro.repository.*;
import com.example.ecogiro.service.AuthService;
import com.example.ecogiro.service.PlanRequestService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserApiController {
    private final VehicleRepository vehicles;
    private final com.example.ecogiro.service.PlanCompatibilityService compatibility;
    private final AuthService authService;
    private final RentalPlanRepository planRepository;
    private final PlanRequestRepository requestRepository;
    private final UserPlanSubscriptionRepository subscriptionRepository;
    private final RecommendationRepository recommendationRepository;
    private final PlanRequestService requestService;

    public UserApiController(AuthService authService,
                             RentalPlanRepository planRepository,
                             PlanRequestRepository requestRepository,
                             UserPlanSubscriptionRepository subscriptionRepository,
                             RecommendationRepository recommendationRepository,
                             PlanRequestService requestService, VehicleRepository vehicles, com.example.ecogiro.service.PlanCompatibilityService compatibility) {
        this.vehicles=vehicles;this.compatibility=compatibility;
        this.authService = authService;
        this.planRepository = planRepository;
        this.requestRepository = requestRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.recommendationRepository = recommendationRepository;
        this.requestService = requestService;
    }

    @GetMapping("/me")
    public UserView me(Authentication authentication) {
        return UserView.from(current(authentication));
    }

    @GetMapping("/plans")
    public List<PlanView> plans() {
        return planRepository.findByActiveTrueOrderByPriceAsc().stream().map(PlanView::from).toList();
    }

    public record CompatibleVehicle(Long id,String code,String model,String type) {}
    @GetMapping("/vehicles")
    public List<CompatibleVehicle> compatibleVehicles(@RequestParam Long planId) {
        RentalPlan plan=planRepository.findById(planId).filter(RentalPlan::isActive)
            .orElseThrow(() -> new IllegalArgumentException("Plano indisponível."));
        return vehicles.findByStatusOrderByLocationNameAsc(VehicleStatus.AVAILABLE).stream()
            .filter(v -> compatibility.accepts(plan.getVehicleCategory(),v.getType()))
            .map(v -> new CompatibleVehicle(v.getId(),v.getCode(),v.getModel(),v.getType().name())).toList();
    }

    @GetMapping("/requests")
    public List<RequestView> requests(Authentication authentication) {
        return requestRepository.findByUserOrderByRequestedAtDesc(current(authentication)).stream()
                .map(RequestView::from).toList();
    }

    @PostMapping("/requests")
    public RequestView requestPlan(@jakarta.validation.Valid @RequestBody com.example.ecogiro.dto.PlanRequestInput body, Authentication authentication) {
        if (body.planId() == null) throw new IllegalArgumentException("Selecione um plano.");
        return RequestView.from(requestService.requestPlan(current(authentication), body.planId(), body.vehicleId(), body.message()));
    }

    @PostMapping("/requests/{id}/cancel")
    public RequestView cancel(@PathVariable Long id, Authentication authentication) {
        return RequestView.from(requestService.cancel(id, current(authentication)));
    }

    @GetMapping("/subscriptions")
    public List<SubscriptionView> subscriptions(Authentication authentication) {
        return subscriptionRepository.findByUserOrderByCreatedAtDesc(current(authentication)).stream()
                .map(SubscriptionView::from).toList();
    }

    @GetMapping("/recommendations")
    public List<RecommendationView> recommendations(Authentication authentication) {
        return recommendationRepository.findTop10ByUserOrderByCreatedAtDesc(current(authentication)).stream()
                .map(RecommendationView::from).toList();
    }

    private AppUser current(Authentication authentication) {
        return authService.requireUser(authentication.getName());
    }


    public record UserView(Long id, String fullName, String cpf, Integer age, String email, String role, boolean active, LocalDateTime createdAt) {
        static UserView from(AppUser user) {
            String maskedCpf = user.getCpf().length() == 11
                    ? user.getCpf().substring(0, 3) + ".***.***-" + user.getCpf().substring(9)
                    : user.getCpf();
            return new UserView(user.getId(), user.getFullName(), maskedCpf, user.getAge(), user.getEmail(), user.getRole().name(), user.isActive(), user.getCreatedAt());
        }
    }

    public record PlanView(Long id, String name, String description, BigDecimal price, Integer durationDays, String vehicleCategory) {
        static PlanView from(RentalPlan plan) {
            return new PlanView(plan.getId(), plan.getName(), plan.getDescription(), plan.getPrice(), plan.getDurationDays(), plan.getVehicleCategory());
        }
    }

    public record RequestView(Long id, Long planId, String planName, String status, String userMessage, String adminResponse, LocalDateTime requestedAt, LocalDateTime respondedAt, String protocol, Long vehicleId, String vehicleModel) {
        static RequestView from(PlanRequest request) {
            return new RequestView(request.getId(), request.getPlan().getId(), request.getPlan().getName(), request.getStatus().name(), request.getUserMessage(), request.getAdminResponse(), request.getRequestedAt(), request.getRespondedAt(), request.getProtocol(), request.getVehicle()==null?null:request.getVehicle().getId(), request.getVehicle()==null?null:request.getVehicle().getModel());
        }
    }

    public record SubscriptionView(Long id, String planName, String status, LocalDate startDate, LocalDate endDate) {
        static SubscriptionView from(UserPlanSubscription subscription) {
            return new SubscriptionView(subscription.getId(), subscription.getPlan().getName(), subscription.getStatus().name(), subscription.getStartDate(), subscription.getEndDate());
        }
    }

    public record RecommendationView(Long id, String principal, String alternativa, LocalDateTime createdAt) {
        static RecommendationView from(Recommendation recommendation) {
            return new RecommendationView(recommendation.getId(), recommendation.getRecomendacaoPrincipal(), recommendation.getRecomendacaoAlternativa(), recommendation.getCreatedAt());
        }
    }
}
