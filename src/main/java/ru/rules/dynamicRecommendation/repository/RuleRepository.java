package ru.rules.dynamicRecommendation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.rules.dynamicRecommendation.model.Rule;
import java.util.UUID;

public interface RuleRepository extends JpaRepository<Rule, UUID> {
}