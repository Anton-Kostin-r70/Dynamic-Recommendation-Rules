package ru.rules.dynamicRecommendation.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.rules.dynamicRecommendation.dto.RuleDTO;
import ru.rules.dynamicRecommendation.dto.RulesDTO;
import ru.rules.dynamicRecommendation.service.RuleService;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST controller for managing rules.
 * Handles HTTP requests related to rule creation, deletion, and retrieval.
 */
@RestController
@RequestMapping("/rule")
@RequiredArgsConstructor
public class RuleController {
    /**
     * Service for business logic operations with rules.
     * Initialized via Lombok's @RequiredArgsConstructor.
     */
    private final RuleService ruleService;
    private static final Logger log = LoggerFactory.getLogger(RuleController.class);

    /**
     * Creates a new rule based on the provided DTO.
     * <p>
     * HTTP POST /rule
     *
     * @param dto Data Transfer Object containing rule data for creation
     * @return ResponseEntity with the created RuleDTO and HTTP status 200 (OK)
     */
    @PostMapping
    @Operation(summary = "Create a new rule",
            description = "Saves a new rule to the system")
    public ResponseEntity<RuleDTO> createRule(@RequestBody RuleDTO dto) {
        RuleDTO created = ruleService.createRule(dto);
        log.info("Created rule with ID: {}", dto.getId());
        return ResponseEntity.ok(created);
    }

    /**
     * Deletes a rule by its unique identifier.
     * <p>
     * Attempts to remove a rule from the database by its UUID.
     * Returns 204 No Content if the rule was successfully deleted,
     * or 404 Not Found if the rule does not exist or could not be deleted.
     * <p>
     * HTTP DELETE /rule/{id}
     *
     * @param id Unique identifier (UUID) of the rule to be deleted
     * @return ResponseEntity with no content and HTTP status 204 (No Content)
     * if the rule was successfully deleted; or status 404 (Not Found)
     * if the rule does not exist
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a rule by UUID",
            description = "Removes a rule from the system by its unique identifier. "
    )
    public ResponseEntity<Void> deleteRule(@PathVariable UUID id) {
        log.info("Attempting to delete rule with ID: {}", id);
        ruleService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves all available rules from the system.
     * <p>
     * HTTP GET /rule
     *
     * @return ResponseEntity containing RulesDTO with a list of all RuleDTO objects
     * and HTTP status 200 (OK)
     */
    @GetMapping
    @Operation(summary = "Retrieve all rules",
            description = "Returns a collection of all rules in the system")
    public ResponseEntity<RulesDTO> getAllRules() {
        List<RuleDTO> rules = ruleService.getAllRules();
        return ResponseEntity.ok(new RulesDTO(rules));
    }
}
