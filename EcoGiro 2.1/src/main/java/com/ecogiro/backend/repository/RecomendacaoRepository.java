package com.ecogiro.backend.repository;

import com.ecogiro.backend.entity.RecomendacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecomendacaoRepository extends JpaRepository<RecomendacaoEntity, Long> {
}