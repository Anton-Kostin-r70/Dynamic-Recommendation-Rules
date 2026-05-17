package ru.rules.dynamicRecommendation.repository.secondary;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.rules.dynamicRecommendation.model.RuleEntity;

import java.util.UUID;

/**
 * Repository interface for managing RuleEntity instances in the dynamic recommendation system.
 * Provides CRUD operations and custom methods for persistence and retrieval of recommendation rules.
 * Extends JpaRepository to leverage Spring Data JPA functionality for database operations.
 */
public interface RuleRepository extends JpaRepository<RuleEntity, UUID> {

    /**
     * Deletes a recommendation rule by its unique identifier.
     *
     * @param id the UUID identifier of the rule to be deleted; must not be null
     * @throws IllegalArgumentException if the provided id is null
     *                                  Example usage:
     *                                  <pre>
     *                                  {@code ruleRepository.deleteById(ruleId);}
     *                                  </pre>
     */
    void deleteById(UUID id);

    boolean existsByProductId(UUID productId);
}