package com.ecogiro.backend.service;

import com.ecogiro.backend.dto.RequisicaoQuizDTO;
import com.ecogiro.backend.dto.RespostaRecomendacaoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RecomendacaoServiceTest {

    private RecomendacaoService service;

    @BeforeEach
    void setUp() {
        service = new RecomendacaoService();
    }

    @Test
    void calcularRecomendacao_deveRetornarMotoQuandoHabilitacaoELongaDistancia() {
        RequisicaoQuizDTO dados = new RequisicaoQuizDTO();
        dados.setHabilitacao("A");
        dados.setDistancia("Longa");

        RespostaRecomendacaoDTO resultado = service.calcularRecomendacao(dados);

        assertNotNull(resultado);
        assertEquals("Motocicleta", resultado.getTitulo());
        assertEquals("src/img/Moto.png", resultado.getIcone());
        assertEquals("#e8542c", resultado.getCor());
        assertEquals("A partir de R$ 89/dia", resultado.getPreco());
        assertEquals(List.of(
                "Ideal para longas distâncias",
                "Alta produtividade para entregas",
                "Maior autonomia da frota"
        ), resultado.getBullets());
        assertEquals("E-bike", resultado.getAlternativaTitulo());
        assertEquals("src/img/e-bike.jpg", resultado.getAlternativaIcone());
    }

    @Test
    void calcularRecomendacao_deveRetornarEBikeQuandoDistanciaMedia() {
        RequisicaoQuizDTO dados = new RequisicaoQuizDTO();
        dados.setHabilitacao("B");
        dados.setDistancia("Média");

        RespostaRecomendacaoDTO resultado = service.calcularRecomendacao(dados);

        assertNotNull(resultado);
        assertEquals("E-bike", resultado.getTitulo());
        assertEquals("src/img/e-bike.jpg", resultado.getIcone());
        assertEquals("#0c9bc9", resultado.getCor());
        assertEquals("A partir de R$ 35/dia", resultado.getPreco());
        assertEquals(List.of(
                "Pedalada assistida, sem esforço extra",
                "Ideal para trajetos médios com conforto",
                "Não exige habilitação"
        ), resultado.getBullets());
        assertEquals("Bicicleta", resultado.getAlternativaTitulo());
        assertEquals("src/img/bike.jpg", resultado.getAlternativaIcone());
    }

    @Test
    void calcularRecomendacao_devePriorizarMotoEmEmpateSemPontuacao() {
        RequisicaoQuizDTO dados = new RequisicaoQuizDTO();
        dados.setHabilitacao("B");
        dados.setDistancia("Curta");

        RespostaRecomendacaoDTO resultado = service.calcularRecomendacao(dados);

        assertNotNull(resultado);
        assertEquals("Motocicleta", resultado.getTitulo());
        assertEquals("src/img/Moto.png", resultado.getIcone());
        assertEquals("E-bike", resultado.getAlternativaTitulo());
    }
}
