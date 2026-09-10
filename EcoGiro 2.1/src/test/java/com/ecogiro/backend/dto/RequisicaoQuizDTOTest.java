package com.ecogiro.backend.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RequisicaoQuizDTOTest {

    @Test
    void shouldExposeValuesUsingSettersAndGetters() {
        RequisicaoQuizDTO dto = new RequisicaoQuizDTO();

        dto.setFinalidade("Trabalho");
        dto.setDistancia("Longa");
        dto.setHabilitacao("A");
        dto.setEsforco("Baixo");
        dto.setOrcamento("Médio");

        assertEquals("Trabalho", dto.getFinalidade());
        assertEquals("Longa", dto.getDistancia());
        assertEquals("A", dto.getHabilitacao());
        assertEquals("Baixo", dto.getEsforco());
        assertEquals("Médio", dto.getOrcamento());
    }
}
