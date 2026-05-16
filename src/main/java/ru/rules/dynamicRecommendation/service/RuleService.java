package ru.rules.dynamicRecommendation.service;

import org.springframework.stereotype.Service;
import ru.rules.dynamicRecommendation.repository.RuleRepository;

@Service
public class RuleService {

    private final RuleRepository ruleRepository;

    public RuleService(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }
}
