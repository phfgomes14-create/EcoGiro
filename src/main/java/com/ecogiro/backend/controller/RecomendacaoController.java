package com.ecogiro.backend.controller;

import com.ecogiro.backend.dto.RequisicaoQuizDTO;
import com.ecogiro.backend.dto.RespostaRecomendacaoDTO;
import com.ecogiro.backend.service.RecomendacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recomendacao")
@CrossOrigin(origins = "*")
public class RecomendacaoController {

    @Autowired
    private RecomendacaoService recomendacaoService;

    @PostMapping
    public RespostaRecomendacaoDTO obterRecomendacao(@RequestBody RequisicaoQuizDTO dados) {

        return recomendacaoService.calcularRecomendacao(dados);

    }
    
}