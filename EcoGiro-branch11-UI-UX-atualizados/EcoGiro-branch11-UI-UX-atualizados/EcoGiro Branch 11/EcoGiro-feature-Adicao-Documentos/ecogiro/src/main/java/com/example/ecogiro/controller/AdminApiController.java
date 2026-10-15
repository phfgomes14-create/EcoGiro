package com.example.ecogiro.controller;

import com.example.ecogiro.model.*;
import com.example.ecogiro.repository.*;
import com.example.ecogiro.service.AuthService;
import com.example.ecogiro.service.PlanRequestService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {
    private final AppUserRepository userRepository;
    private final RentalPlanRepository planRepository;
    private final PlanRequestRepository requestRepository;
    private final VehicleRepository vehicleRepository;
    private final UserPlanSubscriptionRepository subscriptionRepository;
    private final PlanRequestService requestService;
    private final AuthService authService;

    public AdminApiController(AppUserRepository userRepository,
                              RentalPlanRepository planRepository,
                              PlanRequestRepository requestRepository,
                              VehicleRepository vehicleRepository,
                              UserPlanSubscriptionRepository subscriptionRepository,
                              PlanRequestService requestService,
                              AuthService authService) {
        this.userRepository = userRepository;
        this.planRepository = planRepository;
        this.requestRepository = requestRepository;
        this.vehicleRepository = vehicleRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.requestService = requestService;
        this.authService = authService;
    }

    @GetMapping("/summary")
    public Summary summary() {
        return new Summary(
                userRepository.countByRole(Role.USER),
                userRepository.countByRole(Role.ADMIN),
                planRepository.count(),
                requestRepository.countByStatus(PlanRequestStatus.PENDING),
                subscriptionRepository.count(),
                vehicleRepository.count()
        );
    }

    @GetMapping("/users")
    public List<AdminUserView> users() {
        return userRepository.findAllByOrderByCreatedAtDesc().stream().map(AdminUserView::from).toList();
    }

    @PatchMapping("/users/{id}/active")
    public AdminUserView setUserActive(@PathVariable Long id, @RequestBody ActiveUpdate body, Authentication authentication) {
        AppUser user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
        AppUser current = authService.requireUser(authentication.getName());
        if (user.getId().equals(current.getId()) && !body.active()) {
            throw new IllegalStateException("Você não pode desativar sua própria conta administrativa.");
        }
        user.setActive(body.active());
        return AdminUserView.from(userRepository.save(user));
    }

    @GetMapping("/plans")
    public List<AdminPlanView> plans() {
        return planRepository.findAllByOrderByCreatedAtDesc().stream().map(AdminPlanView::from).toList();
    }

    @PostMapping("/plans")
    public AdminPlanView createPlan(@RequestBody PlanPayload body) {
        RentalPlan plan = new RentalPlan();
        applyPlan(plan, body);
        return AdminPlanView.from(planRepository.save(plan));
    }

    @PutMapping("/plans/{id}")
    public AdminPlanView updatePlan(@PathVariable Long id, @RequestBody PlanPayload body) {
        RentalPlan plan = planRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Plano não encontrado."));
        applyPlan(plan, body);
        return AdminPlanView.from(planRepository.save(plan));
    }

    @PatchMapping("/plans/{id}/active")
    public AdminPlanView setPlanActive(@PathVariable Long id, @RequestBody ActiveUpdate body) {
        RentalPlan plan = planRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Plano não encontrado."));
        plan.setActive(body.active());
        return AdminPlanView.from(planRepository.save(plan));
    }

    @GetMapping("/requests")
    public List<AdminRequestView> requests() {
        return requestRepository.findAllByOrderByRequestedAtDesc().stream().map(AdminRequestView::from).toList();
    }

    @PatchMapping("/requests/{id}/respond")
    public AdminRequestView respond(@PathVariable Long id, @RequestBody RequestResponse body, Authentication authentication) {
        PlanRequestStatus status;
        try {
            status = PlanRequestStatus.valueOf(body.status().toUpperCase());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Status inválido.");
        }
        AppUser admin = authService.requireUser(authentication.getName());
        return AdminRequestView.from(requestService.respond(id, status, body.response(), admin));
    }

    @GetMapping("/vehicles")
    public List<AdminVehicleView> vehicles() {
        return vehicleRepository.findAllByOrderByLocationNameAscCodeAsc().stream().map(AdminVehicleView::from).toList();
    }

    @PostMapping("/vehicles")
    public AdminVehicleView createVehicle(@RequestBody VehiclePayload body) {
        if (vehicleRepository.existsByCodeIgnoreCase(body.code())) throw new IllegalArgumentException("Código de veículo já cadastrado.");
        Vehicle vehicle = new Vehicle();
        applyVehicle(vehicle, body);
        return AdminVehicleView.from(vehicleRepository.save(vehicle));
    }

    @PutMapping("/vehicles/{id}")
    public AdminVehicleView updateVehicle(@PathVariable Long id, @RequestBody VehiclePayload body) {
        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado."));
        vehicleRepository.findByCodeIgnoreCase(body.code() == null ? "" : body.code().trim())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> { throw new IllegalArgumentException("Código de veículo já cadastrado."); });
        applyVehicle(vehicle, body);
        return AdminVehicleView.from(vehicleRepository.save(vehicle));
    }

    private void applyPlan(RentalPlan plan, PlanPayload body) {
        if (body.name() == null || body.name().isBlank()) throw new IllegalArgumentException("Nome do plano é obrigatório.");
        if (body.description() == null || body.description().isBlank()) throw new IllegalArgumentException("Descrição do plano é obrigatória.");
        if (body.price() == null || body.price().signum() < 0) throw new IllegalArgumentException("Preço inválido.");
        if (body.durationDays() == null || body.durationDays() < 1) throw new IllegalArgumentException("Duração inválida.");
        plan.setName(body.name().trim());
        plan.setDescription(body.description().trim());
        plan.setPrice(body.price());
        plan.setDurationDays(body.durationDays());
        plan.setVehicleCategory(body.vehicleCategory() == null || body.vehicleCategory().isBlank() ? "TODOS" : body.vehicleCategory().trim().toUpperCase());
        plan.setActive(body.active() == null || body.active());
    }

    private void applyVehicle(Vehicle vehicle, VehiclePayload body) {
        if (body.code() == null || body.code().isBlank()) throw new IllegalArgumentException("Código do veículo é obrigatório.");
        if (body.model() == null || body.model().isBlank()) throw new IllegalArgumentException("Modelo é obrigatório.");
        if (body.latitude() == null || body.latitude() < -90 || body.latitude() > 90) throw new IllegalArgumentException("Latitude inválida.");
        if (body.longitude() == null || body.longitude() < -180 || body.longitude() > 180) throw new IllegalArgumentException("Longitude inválida.");
        try {
            vehicle.setType(VehicleType.valueOf(body.type().toUpperCase()));
            vehicle.setStatus(VehicleStatus.valueOf(body.status().toUpperCase()));
        } catch (Exception ex) {
            throw new IllegalArgumentException("Tipo ou status do veículo inválido.");
        }
        vehicle.setCode(body.code().trim().toUpperCase());
        vehicle.setModel(body.model().trim());
        vehicle.setLatitude(body.latitude());
        vehicle.setLongitude(body.longitude());
        vehicle.setLocationName(body.locationName() == null || body.locationName().isBlank() ? "Local não informado" : body.locationName().trim());
    }

    public record Summary(long users, long admins, long plans, long pendingRequests, long subscriptions, long vehicles) {}
    public record ActiveUpdate(boolean active) {}
    public record PlanPayload(String name, String description, BigDecimal price, Integer durationDays, String vehicleCategory, Boolean active) {}
    public record RequestResponse(String status, String response) {}
    public record VehiclePayload(String code, String model, String type, String status, Double latitude, Double longitude, String locationName) {}

    public record AdminUserView(Long id, String fullName, String email, String cpf, Integer age, String role, boolean active, LocalDateTime createdAt) {
        static AdminUserView from(AppUser user) {
            return new AdminUserView(user.getId(), user.getFullName(), user.getEmail(), user.getCpf(), user.getAge(), user.getRole().name(), user.isActive(), user.getCreatedAt());
        }
    }

    public record AdminPlanView(Long id, String name, String description, BigDecimal price, Integer durationDays, String vehicleCategory, boolean active) {
        static AdminPlanView from(RentalPlan plan) {
            return new AdminPlanView(plan.getId(), plan.getName(), plan.getDescription(), plan.getPrice(), plan.getDurationDays(), plan.getVehicleCategory(), plan.isActive());
        }
    }

    public record AdminRequestView(Long id, Long userId, String userName, String userEmail, Long planId, String planName,
                                   String status, String userMessage, String adminResponse, LocalDateTime requestedAt, LocalDateTime respondedAt) {
        static AdminRequestView from(PlanRequest request) {
            return new AdminRequestView(request.getId(), request.getUser().getId(), request.getUser().getFullName(), request.getUser().getEmail(),
                    request.getPlan().getId(), request.getPlan().getName(), request.getStatus().name(), request.getUserMessage(), request.getAdminResponse(), request.getRequestedAt(), request.getRespondedAt());
        }
    }

    public record AdminVehicleView(Long id, String code, String model, String type, String status, Double latitude, Double longitude, String locationName, LocalDateTime updatedAt) {
        static AdminVehicleView from(Vehicle vehicle) {
            return new AdminVehicleView(vehicle.getId(), vehicle.getCode(), vehicle.getModel(), vehicle.getType().name(), vehicle.getStatus().name(),
                    vehicle.getLatitude(), vehicle.getLongitude(), vehicle.getLocationName(), vehicle.getUpdatedAt());
        }
    }
}
