package com.example.ecogiro.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_setor_administrativo", uniqueConstraints =
        @UniqueConstraint(name = "uk_setor_codigo", columnNames = "code"))
public class AdministrativeSector {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 40)
    private String code;
    @Column(nullable = false, length = 120)
    private String name;
    @Column(nullable = false)
    private boolean active = true;

    public Long getId() { return id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
