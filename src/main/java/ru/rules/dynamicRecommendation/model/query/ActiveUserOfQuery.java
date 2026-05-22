package ru.rules.dynamicRecommendation.model.query;

import ru.rules.dynamicRecommendation.dto.QueryDTO;
import ru.rules.dynamicRecommendation.enums.ProductType;
import ru.rules.dynamicRecommendation.repository.KnowledgeRepository;

import static ru.rules.dynamicRecommendation.enums.QueryType.ACTIVE_USER_OF;

/**
 * Query implementation that checks whether a user is considered "active" for a specific product type.
 * A user is deemed active if they have made at least 5 transactions with the specified product type.
 * <p>
 * Used in recommendation rules to target engaged users or exclude inactive ones. Example use cases:
 * <ul>
 *   <li>Offering premium features to active debit card users</li>
 *   <li>Sending re‑engagement offers to inactive credit card holders</li>
 * </ul>
 * <p>
 * The activity threshold (5 transactions) is hardcoded in the business logic of {@link KnowledgeRepository#isActiveUserOf}.
 *
 * @see KnowledgeRepository#isActiveUserOf
 */
public class ActiveUserOfQuery extends Query {

    private final KnowledgeRepository knowledgeRepository;

    /**
     * Constructs an {@code ActiveUserOfQuery} instance from the provided query data transfer object.
     *
     * @param queryDTO the DTO containing the query configuration; must not be {@code null}.
     *                 The DTO must include:
     *                 <ul>
     *                   <li>{@code query} — must be exactly {@code "ACTIVE_USER_OF"} (validated by the parent constructor)</li>
     *                   <li>{@code arguments} — a list of string parameters that must contain exactly one element:
     *                     <ul>
     *                       <li>the product type as a string (e.g., {@code "DEBIT"}, {@code "CREDIT"}),
     *                           which will be converted to a {@link ProductType} enum by the underlying repository</li>
     *                     </ul>
     *                   </li>
     *                   <li>{@code negate} — a boolean flag that determines whether to negate the evaluation result:
     *                     <ul>
     *                       <li><b>{@code false}</b>: returns {@code true} if the user is active
     *                           (i.e., has ≥5 transactions with the specified product type)</li>
     *                       <li><b>{@code true}</b>: returns {@code true} if the user is NOT active
     *                           (i.e., has <5 transactions with the specified product type)</li>
     *                     </ul>
     *                   </li>
     *                 </ul>
     *
     * @param aKnowledgeRepository the repository providing business logic for user activity checks;
     *                         must not be {@code null}. This repository is used in the {@link #evaluate} method
     *                         to determine the user's activity status.
     *
     * @throws IllegalArgumentException if any of the following conditions are met:
     *                                <ul>
     *                                  <li>{@code queryDTO} is {@code null}</li>
     *                                  <li>the query type from {@code queryDTO.getQuery()} is not {@code "ACTIVE_USER_OF"}</li>
     *                                  <li>the arguments list ({@code queryDTO.getArguments()}) is {@code null} or empty</li>
     *                                  <li>the product type argument cannot be parsed into a valid {@link ProductType}</li>
     *                                </ul>
     * @throws NullPointerException if {@code aKnowledgeRepository} is {@code null}
     */
    public ActiveUserOfQuery(QueryDTO queryDTO, KnowledgeRepository aKnowledgeRepository) {
        super(queryDTO);
        this.knowledgeRepository = aKnowledgeRepository;
        validateArguments(1, ACTIVE_USER_OF);
    }

    /**
     * Evaluates whether the specified user is active for the given product type by counting their transactions.
     * The evaluation follows this logic:
     * <ol>
     *   <li>Extracts the product type from the first argument ({@code arguments.get(0)}).</li>
     *   <li>Uses {@link KnowledgeRepository} to count the user's transactions for that product type.</li>
     *   <li>Checks if the count is at least 5 (activity threshold — user is considered active).</li>
     *   <li>Applies negation if the {@code negate} flag is set.</li>
     * </ol>
     *
     * @param userId the ID of the user to evaluate; must not be null. Used for transaction lookup in {@link KnowledgeRepository}.
     *
     * @return boolean result of the evaluation:
     * <ul>
     * <li><b>true</b>: condition is met
     *     <ul>
     *     <li>user is active (has ≥5 transactions for the product type) when {@code negate = false}</li>
     *     <li>user is not active (has <5 transactions for the product type) when {@code negate = true}</li>
     *     </ul>
     * </li>
     * <li><b>false</b>: condition is not met
     *     <ul>
     *     <li>user is not active (has <5 transactions for the product type) when {@code negate = false}</li>
     *     <li>user is active (has ≥5 transactions for the product type) when {@code negate = true}</li>
     *     </ul>
     * </li>
     * </ul>
     *
     * @throws IllegalArgumentException if:
     *   <ul>
     *   <li>{@code userId} is null</li>
     *   <li>{@code arguments} list is empty</li>
     *   <li>the product type argument is invalid (null, empty, or cannot be parsed to a valid {@link ProductType})</li>
     *   </ul>
     * @throws RuntimeException if database access fails in {@link KnowledgeRepository}
     *        (e.g., connection issues, query execution errors, or data retrieval problems)
     */
    @Override
    public boolean evaluate(Long userId) {
        boolean result = knowledgeRepository.isActiveUserOf(userId, ProductType.fromType(arguments.get(0)));
        return negate != result;
    }
}