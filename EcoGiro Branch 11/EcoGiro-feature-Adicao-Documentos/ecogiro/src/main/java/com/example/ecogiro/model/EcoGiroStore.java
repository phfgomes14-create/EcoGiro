package com.example.ecogiro.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_loja_ecogiro", uniqueConstraints =
        @UniqueConstraint(name = "uk_loja_codigo", columnNames = "code"))
public class EcoGiroStore {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 40)
    private String code;
    @Column(nullable = false, length = 120)
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_id", nullable = false)
    private AdministrativeSector sector;
    @Column(nullable = false)
    private Double latitude;
    @Column(nullable = false)
    private Double longitude;
    @Column(nullable = false)
    private boolean active = true;

    private Integer capacity = 20;
    private Boolean blocked = false;
    public int getCapacity() { return capacity == null ? 20 : capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public boolean isBlocked() { return Boolean.TRUE.equals(blocked); }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public AdministrativeSector getSector() { return sector; }
    public void setSector(AdministrativeSector sector) { this.sector = sector; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
