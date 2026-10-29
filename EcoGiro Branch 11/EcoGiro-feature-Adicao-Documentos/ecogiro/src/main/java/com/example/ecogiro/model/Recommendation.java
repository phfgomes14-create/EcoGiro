package com.example.ecogiro.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_RECOMENDACAO")
public class Recommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String distancia;
    private String esforco;
    private String finalidade;
    private String habilitacao;
    private String orcamento;
    private String recomendacaoPrincipal;
    private String recomendacaoAlternativa;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private AppUser user;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public String getDistancia() { return distancia; }
    public void setDistancia(String distancia) { this.distancia = distancia; }
    public String getEsforco() { return esforco; }
    public void setEsforco(String esforco) { this.esforco = esforco; }
    public String getFinalidade() { return finalidade; }
    public void setFinalidade(String finalidade) { this.finalidade = finalidade; }
    public String getHabilitacao() { return habilitacao; }
    public void setHabilitacao(String habilitacao) { this.habilitacao = habilitacao; }
    public String getOrcamento() { return orcamento; }
    public void setOrcamento(String orcamento) { this.orcamento = orcamento; }
    public String getRecomendacaoPrincipal() { return recomendacaoPrincipal; }
    public void setRecomendacaoPrincipal(String recomendacaoPrincipal) { this.recomendacaoPrincipal = recomendacaoPrincipal; }
    public String getRecomendacaoAlternativa() { return recomendacaoAlternativa; }
    public void setRecomendacaoAlternativa(String recomendacaoAlternativa) { this.recomendacaoAlternativa = recomendacaoAlternativa; }
    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
