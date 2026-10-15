package com.example.ecogiro.controller;

import com.example.ecogiro.model.AppUser;
import com.example.ecogiro.model.Recommendation;
import com.example.ecogiro.repository.AppUserRepository;
import com.example.ecogiro.repository.RecommendationRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class RecommendationController {
    private final RecommendationRepository recommendationRepository;
    private final AppUserRepository userRepository;

    public RecommendationController(RecommendationRepository recommendationRepository, AppUserRepository userRepository) {
        this.recommendationRepository = recommendationRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/api/recommendations")
    public Map<String, Object> save(@RequestBody RecommendationPayload body, Authentication authentication) {
        Recommendation recommendation = new Recommendation();
        recommendation.setDistancia(body.distancia());
        recommendation.setEsforco(body.esforco());
        recommendation.setFinalidade(body.finalidade());
        recommendation.setHabilitacao(body.habilitacao());
        recommendation.setOrcamento(body.orcamento());
        recommendation.setRecomendacaoPrincipal(body.recomendacaoPrincipal());
        recommendation.setRecomendacaoAlternativa(body.recomendacaoAlternativa());

        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            AppUser user = userRepository.findByEmailIgnoreCase(authentication.getName()).orElse(null);
            recommendation.setUser(user);
        }

        Recommendation saved = recommendationRepository.save(recommendation);
        return Map.of("id", saved.getId(), "saved", true);
    }

    public record RecommendationPayload(String distancia, String esforco, String finalidade, String habilitacao,
                                        String orcamento, String recomendacaoPrincipal, String recomendacaoAlternativa) {}
}
