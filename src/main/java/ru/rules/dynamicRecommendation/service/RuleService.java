package ru.rules.dynamicRecommendation.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rules.dynamicRecommendation.dto.QueryDTO;
import ru.rules.dynamicRecommendation.dto.RuleDTO;
import ru.rules.dynamicRecommendation.model.QueryEntity;
import ru.rules.dynamicRecommendation.model.RuleEntity;
import ru.rules.dynamicRecommendation.repository.RuleRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service layer class for managing recommendation rules in the dynamic recommendation system.
 * Provides business logic and orchestration for rule operations,
 * including CRUD operations and additional validation/processing.
 * Acts as an intermediary between controllers and repositories.
 */
@Service
public class RuleService {

    private final RuleRepository ruleRepository;

    /**
     * Constructor for dependency injection of RuleRepository.
     *
     * @param ruleRepository the repository for persistence operations on RuleEntity;
     *                       must not be null
     * @throws IllegalArgumentException if ruleRepository is null
     */
    public RuleService(RuleRepository ruleRepository) {
        if (ruleRepository == null) {
            throw new IllegalArgumentException("RuleRepository cannot be null");
        }
        this.ruleRepository = ruleRepository;
    }

    /**
     * Creates and saves a new recommendation rule.
     *
     * @param rule the RuleEntity to be saved; must not be null
     * @return the saved RuleEntity with generated ID and other database‑assigned fields
     * @throws IllegalArgumentException if the rule is null
     */
    public RuleEntity createRule(RuleEntity rule) {
        if (rule == null) {
            throw new IllegalArgumentException("Rule cannot be null");
        }
        return ruleRepository.save(rule);
    }

    /**
     * Retrieves a recommendation rule by its unique identifier.
     *
     * @param id the UUID identifier of the rule to retrieve; must not be null
     * @return Optional containing the RuleEntity if found, empty Optional otherwise
     * @throws IllegalArgumentException if the id is null
     */
    public Optional<RuleEntity> getRuleById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Rule ID cannot be null");
        }
        return ruleRepository.findById(id);
    }

    /**
     * Retrieves all recommendation rules from the system and converts them to DTO representation.
     * <p>
     * This method:
     * <ol>
     *   <li>Fetches all RuleEntity instances from the database via the repository.</li>
     *   <li>Converts each RuleEntity to its corresponding RuleDTO using the internal mapping logic.</li>
     *   <li>Returns a list of RuleDTO objects ready for API exposure.</li>
     * </ol>
     *
     * @return a list of all RuleDTO instances representing recommendation rules in the system;
     * never returns null — returns an empty list if no rules are present in the database
     * @apiNote The conversion from RuleEntity to RuleDTO includes nested QueryEntity objects,
     * which are mapped to QueryDTO objects as part of the transformation process.
     * @implSpec The method uses Java 8 Stream API for efficient transformation:
     * {@code ruleRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList())}
     * @see #mapToDto(RuleEntity) for the mapping logic between entity and DTO
     */
    @Transactional(readOnly = true)
    public List<RuleDTO> getAllRules() {
        return ruleRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing recommendation rule.
     * The rule must already exist in the database (have a valid ID).
     *
     * @param rule the RuleEntity with updated fields; must not be null and must have an ID
     * @return the updated RuleEntity
     * @throws IllegalArgumentException if the rule is null or has no ID
     */
    public RuleEntity updateRule(RuleEntity rule) {
        if (rule == null) {
            throw new IllegalArgumentException("Rule cannot be null");
        }
        if (rule.getId() == null) {
            throw new IllegalArgumentException("Rule must have an ID to be updated");
        }
        return ruleRepository.save(rule);
    }

    /**
     * Checks whether a rule with the specified identifier exists.
     *
     * @param id the UUID identifier to check; must not be null
     * @return true if a rule with the given ID exists, false otherwise
     * @throws IllegalArgumentException if the id is null
     */
    public boolean ruleExists(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Rule ID cannot be null");
        }
        return ruleRepository.existsById(id);
    }

    /**
     * Checks whether any rule exists for the specified product ID.
     *
     * @param productId the UUID of the product to check for associated rules; must not be null
     * @return true if at least one rule exists for the given product ID, false otherwise
     * @throws IllegalArgumentException if productId is null
     */
    @Transactional(readOnly = true)
    public boolean existsByProductId(UUID productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        return ruleRepository.existsByProductId(productId);
    }

    /**
     * Creates a new recommendation rule from a DTO representation.
     * Maps the DTO to an entity, sets a new ID (to ensure creation), and saves it.
     *
     * @param dto the RuleDTO containing rule data; must not be null
     * @return the created RuleDTO with generated ID
     * @throws IllegalArgumentException if dto is null
     */
    @Transactional
    public RuleDTO createRule(RuleDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("DTO cannot be null");
        }
        RuleEntity rule = mapToRule(dto);
        rule.setId(null);
        return mapToDto(ruleRepository.save(rule));
    }

    /**
     * Converts a RuleDTO to a RuleEntity.
     * Handles nested QueryDTO objects by mapping them to QueryEntity objects.
     *
     * @param dto the DTO to convert; may be null
     * @return corresponding RuleEntity, or null if input is null
     */
    private RuleEntity mapToRule(RuleDTO dto) {
        if (dto == null) return null;

        RuleEntity entity = new RuleEntity();
        entity.setId(dto.getId());
        entity.setProductName(dto.getProductName());
        entity.setProductId(dto.getProductId());
        entity.setProductText(dto.getProductText());
        entity.setCreatedAt(LocalDateTime.now());

        if (dto.getQueriesDTO() != null) {
            entity.setQueries(dto.getQueriesDTO().stream()
                    .map(this::dtoToQuery)
                    .collect(Collectors.toList()));
        }
        return entity;
    }

    /**
     * Converts a RuleEntity to a RuleDTO.
     * Handles nested QueryEntity objects by mapping them to QueryDTO objects.
     *
     * @param rule the entity to convert; may be null
     * @return corresponding RuleDTO, or null if input is null
     */
    private RuleDTO mapToDto(RuleEntity rule) {
        if (rule == null) {
            return null;
        }
        RuleDTO dto = new RuleDTO();
        dto.setId(rule.getId());
        dto.setProductName(rule.getProductName());
        dto.setProductId(rule.getProductId());
        dto.setProductText(rule.getProductText());

        if (rule.getQueries() != null) {
            dto.setQueriesDTO(rule.getQueries().stream()
                    .map(this::queryToDTO)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    /**
     * Converts a QueryDTO to a QueryEntity.
     *
     * @param dto the QueryDTO to convert; must not be null
     * @return corresponding QueryEntity
     */
    private QueryEntity dtoToQuery(QueryDTO dto) {
        QueryEntity query = new QueryEntity();
        query.setQueryType(dto.getQuery());
        query.setArguments(dto.getArguments());
        query.setNegate(dto.isNegate());
        return query;
    }

    /**
     * Converts a QueryEntity to a QueryDTO.
     *
     * @param query the QueryEntity to convert; must not be null
     * @return corresponding QueryDTO
     */
    private QueryDTO queryToDTO(QueryEntity query) {
        QueryDTO dto = new QueryDTO();
        dto.setQuery(query.getQueryType());
        dto.setArguments(query.getArguments());
        dto.setNegate(query.isNegate());
        return dto;
    }

    /**
     * Updates an existing recommendation rule from a DTO representation.
     * Ensures the rule exists before updating and maps DTO fields to entity fields.
     *
     * @param dto the RuleDTO containing updated rule data; must not be null and must have an ID
     * @return the updated RuleDTO
     * @throws IllegalArgumentException if dto is null or has no ID
     * @throws EntityNotFoundException  if rule with given ID does not exist
     */
    @Transactional
    public RuleDTO updateRule(RuleDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("DTO cannot be null");
        }
        if (dto.getId() == null) {
            throw new IllegalArgumentException("Rule ID cannot be null for update operation");
        }

        Optional<RuleEntity> existingRule = ruleRepository.findById(dto.getId());
        if (!existingRule.isPresent()) {
            throw new EntityNotFoundException("Rule not found with ID: " + dto.getId());
        }

        RuleEntity rule = mapToRule(dto);
        RuleEntity updatedRule = ruleRepository.save(rule);
        return mapToDto(updatedRule);
    }

    /**
     * Retrieves a recommendation rule by its unique identifier and converts it to DTO format.
     *
     * @param id the UUID identifier of the rule to retrieve; must not be null
     * @return Optional containing the RuleDTO if found, empty Optional otherwise
     * @throws IllegalArgumentException if the id is null
     */
    @Transactional(readOnly = true)
    public Optional<RuleDTO> getRuleDtoById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Rule ID cannot be null");
        }
        return getRuleById(id)
                .map(this::mapToDto);
    }

    /**
     * Deletes a rule by ID.
     *
     * @param id UUID of the rule to delete
     */
    @Transactional
    public void deleteRule(UUID id) {
        ruleRepository.deleteById(id);
    }
}
