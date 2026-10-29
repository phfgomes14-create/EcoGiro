package com.example.ecogiro.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> integrityConflict(org.springframework.dao.DataIntegrityViolationException ex) {
        String cause = String.valueOf(ex.getMostSpecificCause().getMessage()).toLowerCase(java.util.Locale.ROOT);
        String message = cause.contains("uk_setor_codigo")
            ? "Já existe um setor com esse código. Selecione o setor existente ou use outro código."
            : cause.contains("uk_loja_codigo")
            ? "Já existe uma loja com esse código. Selecione a loja existente ou use outro código."
            : "Os dados conflitam com um cadastro existente ou vínculo obrigatório. Confira os dados informados.";
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", message));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> conflict(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
    }
}
