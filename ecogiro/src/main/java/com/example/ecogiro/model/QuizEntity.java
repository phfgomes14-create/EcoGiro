package com.example.ecogiro.model;

import jakarta.persistence.*;

@Entity
@Table(name = "respostas_quiz")
public class QuizEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String emailUsuario;
    private int pontuacao;
    private String resultado;

    public QuizEntity() {}

    public QuizEntity(String emailUsuario, int pontuacao, String resultado) {
        this.emailUsuario = emailUsuario;
        this.pontuacao = pontuacao;
        this.resultado = resultado;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmailUsuario() { return emailUsuario; }
    public void setEmailUsuario(String emailUsuario) { this.emailUsuario = emailUsuario; }

    public int getPontuacao() { return pontuacao; }
    public void setPontuacao(int pontuacao) { this.pontuacao = pontuacao; }

    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }
}