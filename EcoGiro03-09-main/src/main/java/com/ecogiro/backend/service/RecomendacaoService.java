package com.ecogiro.backend.service;

import com.ecogiro.backend.dto.RequisicaoQuizDTO;
import com.ecogiro.backend.dto.RespostaRecomendacaoDTO;
import com.ecogiro.backend.entity.RecomendacaoEntity;
import com.ecogiro.backend.repository.RecomendacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class RecomendacaoService {

    @Autowired
    private RecomendacaoRepository recomendacaoRepository;

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
            else if (dados.getDistancia().contains("Muito Longa")) pontuacaoMoto += 2;

        }

        RespostaRecomendacaoDTO resposta;

        if (pontuacaoMoto > pontuacaoEbike && pontuacaoMoto > pontuacaoBicicleta) {

            resposta = RespostaRecomendacaoDTO.builder()
                    .titulo("Motocicleta")
                    .icone("src/img/Moto.png")
                    .cor("#A8542C")
                    .preco("A partir de R$ 89/dia")
                    .bullets(Arrays.asList("Ideal para longas distâncias", "Alta produtividade para entregas", "Maior autonomia da frota"))
                    .alternativaTitulo("E-bike")
                    .alternativaIcone("src/img/e-bike.jpg")

                    .build();
        } else {
        
            resposta = RespostaRecomendacaoDTO.builder()
                    .titulo("E-bike")
                    .icone("src/img/e-bike.jpg")
                    .cor("#0C9BC9")
                    .preco("A partir de R$ 35/dia")
                    .bullets(Arrays.asList("Pedalada assistida, sem esforço extra", "Ideal para trajetos médios com conforto", "Não exige habilitação"))
                    .alternativaTitulo("Bicicleta")
                    .alternativaIcone("src/img/bike.jpg")
                    .build();

        }

        // Salva os dados no banco H2
        RecomendacaoEntity entidade = new RecomendacaoEntity();
        entidade.setFinalidade(dados.getFinalidade());
        entidade.setDistancia(dados.getDistancia());
        entidade.setHabilitacao(dados.getHabilitacao());
        entidade.setEsforco(dados.getEsforco());
        entidade.setOrcamento(dados.getOrcamento());
        entidade.setRecomendacaoPrincipal(resposta.getTitulo());
        entidade.setRecomendacaoAlternativa(resposta.getAlternativaTitulo());

        recomendacaoRepository.save(entidade);

        return resposta;

    }

}