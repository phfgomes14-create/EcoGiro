package com.ecogiro.backend.controller;

import com.ecogiro.backend.dto.RequisicaoQuizDTO;
import com.ecogiro.backend.dto.RespostaRecomendacaoDTO;
import com.ecogiro.backend.service.RecomendacaoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recomendacao")
@CrossOrigin(origins = "*")
public class RecomendacaoController {

    private final RecomendacaoService recomendacaoService;

    public RecomendacaoController(RecomendacaoService recomendacaoService) {
        this.recomendacaoService = recomendacaoService;
    }

    @PostMapping
    public RespostaRecomendacaoDTO obterRecomendacao(@RequestBody RequisicaoQuizDTO requisicao) {
        return recomendacaoService.calcularRecomendacao(requisicao);
    }
}