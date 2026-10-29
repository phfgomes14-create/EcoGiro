package com.example.ecogiro.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_atribuicao_setor", uniqueConstraints =
        @UniqueConstraint(name = "uk_atribuicao_admin_setor", columnNames = {"administrator_id", "sector_id"}))
public class AdministratorSectorAssignment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "administrator_id", nullable = false)
    private AppUser administrator;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_id", nullable = false)
    private AdministrativeSector sector;
    @Column(nullable = false, updatable = false)
    private LocalDateTime assignedAt = LocalDateTime.now();

    public Long getId() { return id; }
    public AppUser getAdministrator() { return administrator; }
    public void setAdministrator(AppUser administrator) { this.administrator = administrator; }
    public AdministrativeSector getSector() { return sector; }
    public void setSector(AdministrativeSector sector) { this.sector = sector; }
    public LocalDateTime getAssignedAt() { return assignedAt; }
}
