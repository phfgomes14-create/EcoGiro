package com.example.ecogiro.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_SOLICITACAO_PLANO", uniqueConstraints = @UniqueConstraint(name = "uk_solicitacao_protocolo", columnNames = "protocol"))
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

    // Nullable para preservar registros anteriores durante a atualização de esquema.
    @Column(length = 36, updatable = false)
    private String protocol;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "veiculo_id")
    private Vehicle vehicle;

    @PrePersist
    void generateProtocol() { protocol = java.util.UUID.randomUUID().toString(); }

    public String getProtocol() { return protocol == null && id != null ? "LEGACY-" + id : protocol; }
    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }

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
