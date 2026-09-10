package com.ecogiro.backend.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RespostaRecomendacaoDTOTest {

    @Test
    void builder_shouldCreateDtoWithExpectedValues() {
        RespostaRecomendacaoDTO dto = RespostaRecomendacaoDTO.builder()
                .titulo("Motocicleta")
                .icone("src/img/Moto.png")
                .cor("#e8542c")
                .preco("A partir de R$ 89/dia")
                .bullets(List.of("Ideal para longas distâncias", "Maior autonomia da frota"))
                .alternativaTitulo("E-bike")
                .alternativaIcone("src/img/e-bike.jpg")
                .build();

        assertAll(
                () -> assertEquals("Motocicleta", dto.getTitulo()),
                () -> assertEquals("src/img/Moto.png", dto.getIcone()),
                () -> assertEquals("#e8542c", dto.getCor()),
                () -> assertEquals("A partir de R$ 89/dia", dto.getPreco()),
                () -> assertEquals(List.of("Ideal para longas distâncias", "Maior autonomia da frota"), dto.getBullets()),
                () -> assertEquals("E-bike", dto.getAlternativaTitulo()),
                () -> assertEquals("src/img/e-bike.jpg", dto.getAlternativaIcone())
        );
    }
}
