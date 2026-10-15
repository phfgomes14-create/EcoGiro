package com.example.ecogiro.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_proposta_redistribuicao",
       indexes = @Index(name = "idx_proposta_status", columnList = "status"))
public class SectorRedistributionProposal {
    public enum Status { PENDING, APPROVED, REJECTED, CANCELLED }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_id", nullable = false)
    private AdministrativeSector sector;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_admin_id")
    private AppUser fromAdministrator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_admin_id", nullable = false)
    private AppUser toAdministrator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by_id", nullable = false)
    private AppUser requestedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decided_by_id")
    private AppUser decidedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.PENDING;

    @Column(nullable = false, length = 500)
    private String reason;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime decidedAt;

    public Long getId() { return id; }
    public AdministrativeSector getSector() { return sector; }
    public void setSector(AdministrativeSector sector) { this.sector = sector; }
    public AppUser getFromAdministrator() { return fromAdministrator; }
    public void setFromAdministrator(AppUser fromAdministrator) { this.fromAdministrator = fromAdministrator; }
    public AppUser getToAdministrator() { return toAdministrator; }
    public void setToAdministrator(AppUser toAdministrator) { this.toAdministrator = toAdministrator; }
    public AppUser getRequestedBy() { return requestedBy; }
    public void setRequestedBy(AppUser requestedBy) { this.requestedBy = requestedBy; }
    public AppUser getDecidedBy() { return decidedBy; }
    public void setDecidedBy(AppUser decidedBy) { this.decidedBy = decidedBy; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getDecidedAt() { return decidedAt; }
    public void setDecidedAt(LocalDateTime decidedAt) { this.decidedAt = decidedAt; }
}
