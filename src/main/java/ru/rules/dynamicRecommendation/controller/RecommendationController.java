package ru.rules.dynamicRecommendation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.rules.dynamicRecommendation.dto.RuleDTO;
import ru.rules.dynamicRecommendation.service.RuleEvaluatorService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/recommendation")
public class RecommendationController {

    private final RuleEvaluatorService ruleEvaluatorService;

    public RecommendationController(RuleEvaluatorService ruleEvaluatorService) {
        this.ruleEvaluatorService = ruleEvaluatorService;
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<List<RuleDTO>> getRecommendations(
            @PathVariable("user_id") Long userId) {
        return ResponseEntity.ok(ruleEvaluatorService.getRecommendations(userId));
    }
}
