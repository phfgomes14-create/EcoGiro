package com.example.ecogiro.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_SOLICITACAO_PLANO")
public class PlanRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private AppUser user;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "plano_id", nullable = false)
    private RentalPlan plan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PlanRequestStatus status = PlanRequestStatus.PENDING;

    @Column(length = 500)
    private String userMessage;

    @Column(length = 500)
    private String adminResponse;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "respondido_por")
    private AppUser respondedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime requestedAt = LocalDateTime.now();

    private LocalDateTime respondedAt;

    public Long getId() { return id; }
    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }
    public RentalPlan getPlan() { return plan; }
    public void setPlan(RentalPlan plan) { this.plan = plan; }
    public PlanRequestStatus getStatus() { return status; }
    public void setStatus(PlanRequestStatus status) { this.status = status; }
    public String getUserMessage() { return userMessage; }
    public void setUserMessage(String userMessage) { this.userMessage = userMessage; }
    public String getAdminResponse() { return adminResponse; }
    public void setAdminResponse(String adminResponse) { this.adminResponse = adminResponse; }
    public AppUser getRespondedBy() { return respondedBy; }
    public void setRespondedBy(AppUser respondedBy) { this.respondedBy = respondedBy; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public LocalDateTime getRespondedAt() { return respondedAt; }
    public void setRespondedAt(LocalDateTime respondedAt) { this.respondedAt = respondedAt; }
}
