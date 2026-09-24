package com.example.ecogiro.controller;

import com.example.ecogiro.model.QuizEntity;
import com.example.ecogiro.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    @Autowired
    private QuizRepository quizRepository;

    @PostMapping("/salvar")
    public ResponseEntity<?> salvarResposta(@RequestBody QuizEntity quizData) {
        QuizEntity salvo = quizRepository.save(quizData);
        return ResponseEntity.ok(salvo);
    }

    @GetMapping("/respostas")
    public ResponseEntity<List<QuizEntity>> listarRespostas() {
        return ResponseEntity.ok(quizRepository.findAll());
    }
}