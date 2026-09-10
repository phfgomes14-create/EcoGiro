package com.ecogiro.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequisicaoQuizDTO {
    private String finalidade;
    private String distancia;
    private String habilitacao;
    private String esforco;
    private String orcamento;
}