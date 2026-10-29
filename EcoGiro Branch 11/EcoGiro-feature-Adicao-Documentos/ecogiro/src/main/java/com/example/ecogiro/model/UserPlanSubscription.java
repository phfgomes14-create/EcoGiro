package com.example.ecogiro.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_ASSINATURA_PLANO")
public class UserPlanSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private AppUser user;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "plano_id", nullable = false)
    private RentalPlan plan;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "solicitacao_id", nullable = false, unique = true)
    private PlanRequest request;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubscriptionStatus status = SubscriptionStatus.ACTIVE;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }
    public RentalPlan getPlan() { return plan; }
    public void setPlan(RentalPlan plan) { this.plan = plan; }
    public PlanRequest getRequest() { return request; }
    public void setRequest(PlanRequest request) { this.request = request; }
    public SubscriptionStatus getStatus() { return status; }
    public void setStatus(SubscriptionStatus status) { this.status = status; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
