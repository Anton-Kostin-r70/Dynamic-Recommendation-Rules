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
     * Creates a {@code Query} instance of the appropriate type based on the provided {@code QueryDTO} and repository.
     * Delegates to concrete query constructors, passing the DTO and knowledge repository for business logic.
     *
     * @param queryDTO            the data transfer object containing the query configuration; must not be {@code null}.
     *                            The DTO must include:
     *                            <ul>
     *                              <li>{@code query} — a query type identifier (e.g., {@code "USER_OF"}, {@code "ACTIVE_USER_OF"})
     *                                  that determines the concrete query implementation. This value will be parsed into a
     *                                  {@link QueryType} enum.</li>
     *                              <li>{@code arguments} — a list of string parameters required for query execution.
     *                                  The required number and format of arguments depend on the specific {@code queryType}.</li>
     *                              <li>{@code negate} — a boolean flag indicating whether to negate the evaluation result of the query.</li>
     *                            </ul>
     * @param knowledgeRepository the repository providing business logic for various query evaluations;
     *                            must not be {@code null}. This repository is passed to the created query instance
     *                            and is used in its {@link Query#evaluate} method.
     * @return a fully configured {@code Query} instance of the corresponding type (e.g., {@code UserOfQuery},
     * {@code ActiveUserOfQuery}, {@code TransactionSumCompareQuery}).
     * The returned query is ready to be evaluated via the {@link Query#evaluate} method.
     * @throws IllegalArgumentException if any of the following conditions are met:
     *                                  <ul>
     *                                    <li>{@code queryDTO} is {@code null}</li>
     *                                    <li>the query type from {@code queryDTO.getQuery()} is invalid and cannot be converted
     *                                        to a {@link QueryType} enum value (e.g., due to an unrecognized string)</li>
     *                                    <li>the parsed {@code queryType} is not supported by this factory method
     *                                        (i.e., no matching case in the switch statement, resulting in the {@code default} branch)</li>
     *                                  </ul>
     * @throws NullPointerException     if {@code knowledgeRepository} is {@code null}
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