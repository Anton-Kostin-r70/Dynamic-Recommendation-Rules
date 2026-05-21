package ru.rules.dynamicRecommendation.model.query;

import ru.rules.dynamicRecommendation.dto.QueryDTO;
import ru.rules.dynamicRecommendation.enums.QueryType;
import ru.rules.dynamicRecommendation.repository.KnowledgeRepository;

/**
 * Factory class for creating query instances based on the specified query type.
 * Provides a centralized mechanism for instantiating different types of queries
 * used in the dynamic recommendation system.
 * <p>
 * Uses the Factory Method pattern to encapsulate object creation logic
 * and decouple client code from concrete query implementations.
 */
public class QueryFactory {

    /**
     * Creates a query instance of the appropriate type based on the provided QueryDTO and repository.
     * Delegates to concrete query constructors, passing the DTO and knowledge repository for business logic.
     *
     * @param queryDTO data transfer object containing query configuration; must not be null.
     *               The DTO should provide:
     *               <ul>
     *               <li>{@code query} — query type identifier (e.g., "USER_OF", "ACTIVE_USER_OF")
     *                   that determines the concrete query implementation</li>
     *               <li>{@code arguments} — list of string parameters required for query execution</li>
     *               <li>{@code negate} — flag indicating whether to negate the query result</li>
     *               </ul>
     * @param knowledgeRepository repository providing business logic for various query evaluations;
     *                        must not be null. This repository will be passed to the created query instance
     *                        for use in its {@code evaluate} method.
     * @return a {@code Query} instance of the corresponding type (e.g., {@code UserOfQuery},
     *         {@code ActiveUserOfQuery}), configured with the provided parameters and repository.
     *         The returned query is ready for evaluation via {@link Query#evaluate}.
     * @throws IllegalArgumentException if:
     *        <ul>
     *        <li>{@code queryDTO} is null</li>
     *        <li>the query type extracted from {@code queryDTO.getQuery()} is invalid or cannot be converted
     *            to a {@link QueryType} enum value</li>
     *        <li>the specified {@code queryType} is not supported (no matching case in the switch statement)</li>
     *        </ul>
     * @throws NullPointerException if {@code knowledgeRepository} is null
     */
    public static Query createQuery(QueryDTO queryDTO, KnowledgeRepository knowledgeRepository) {
        QueryType queryType = QueryType.valueOf(queryDTO.getQuery());
        return switch (queryType) {
            case USER_OF -> new UserOfQuery(queryDTO, knowledgeRepository);
            case ACTIVE_USER_OF -> new ActiveUserOfQuery(queryDTO, knowledgeRepository);
            case TRANSACTION_SUM_COMPARE -> new TransactionSumCompareQuery(queryDTO, knowledgeRepository);
            case TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW ->
                    new TransactionSumCompareDepositWithdrawQuery(queryDTO, knowledgeRepository);
            default -> throw new IllegalArgumentException("Unknown query type: " + queryType);
        };
    }
}