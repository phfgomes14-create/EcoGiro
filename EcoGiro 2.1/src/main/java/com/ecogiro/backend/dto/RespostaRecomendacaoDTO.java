package com.ecogiro.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RespostaRecomendacaoDTO {
    private String titulo;
    private String icone;
    private String cor;
    private String preco;
    private List<String> bullets;
    private String alternativaTitulo;
    private String alternativaIcone;
}