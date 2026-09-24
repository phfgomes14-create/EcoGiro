package com.example.ecogiro.repository;

import com.example.ecogiro.model.QuizEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<QuizEntity, Long> {
}