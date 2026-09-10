package com.ecogiro.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_recomendacao")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecomendacaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500)
    private String finalidade;

    @Column(length = 500)
    private String distancia;

    @Column(length = 500)
    private String habilitacao;

    @Column(length = 500)
    private String esforco;

    @Column(length = 500)
    private String orcamento;

    private String recomendacaoPrincipal;
    private String recomendacaoAlternativa;
}