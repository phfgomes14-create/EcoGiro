package com.ecogiro.backend.service;

import com.ecogiro.backend.dto.RequisicaoQuizDTO;
import com.ecogiro.backend.dto.RespostaRecomendacaoDTO;
import org.springframework.stereotype.Service;
import java.util.Arrays;

@Service
public class RecomendacaoService {

    public RespostaRecomendacaoDTO calcularRecomendacao(RequisicaoQuizDTO dados) {
        int pontuacaoMoto = 0;
        int pontuacaoEbike = 0;
        int pontuacaoBicicleta = 0;
        int pontuacaoPatinete = 0;

        if (dados.getHabilitacao() != null && dados.getHabilitacao().contains("A")) {
            pontuacaoMoto += 3;
        }

        if (dados.getDistancia() != null) {
            if (dados.getDistancia().contains("Curta")) pontuacaoPatinete += 2;
            else if (dados.getDistancia().contains("Média")) pontuacaoEbike += 2;
            else if (dados.getDistancia().contains("Longa")) pontuacaoBicicleta += 2;
            else if (dados.getDistancia().contains("Muito longa")) pontuacaoMoto += 2;
        }

        if (pontuacaoMoto >= pontuacaoEbike && pontuacaoMoto >= pontuacaoBicicleta) {
            return RespostaRecomendacaoDTO.builder()
                    .titulo("Motocicleta")
                    .icone("src/img/Moto.png")
                    .cor("#e8542c")
                    .preco("A partir de R$ 89/dia")
                    .bullets(Arrays.asList("Ideal para longas distâncias", "Alta produtividade para entregas", "Maior autonomia da frota"))
                    .alternativaTitulo("E-bike")
                    .alternativaIcone("src/img/e-bike.jpg")
                    .build();
        }

        return RespostaRecomendacaoDTO.builder()
                .titulo("E-bike")
                .icone("src/img/e-bike.jpg")
                .cor("#0c9bc9")
                .preco("A partir de R$ 35/dia")
                .bullets(Arrays.asList("Pedalada assistida, sem esforço extra", "Ideal para trajetos médios com conforto", "Não exige habilitação"))
                .alternativaTitulo("Bicicleta")
                .alternativaIcone("src/img/bike.jpg")
                .build();
    }
}