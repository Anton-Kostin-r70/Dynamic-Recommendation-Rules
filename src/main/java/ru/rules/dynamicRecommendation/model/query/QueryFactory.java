package ru.rules.dynamicRecommendation.model.query;

import ru.rules.dynamicRecommendation.enums.QueryType;

import java.util.List;

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
     * Creates a query instance of the appropriate type based on the provided parameters.
     *
     * @param queryType the type of query to create (determines the concrete implementation)
     * @param arguments list of string arguments required for query execution;
     *                  the structure and meaning of arguments depend on the query type
     * @param negate    flag indicating whether to negate the query result
     *                  (true = invert the result, false = use original result)
     * @return a Query instance of the corresponding type configured with the provided parameters
     * @throws IllegalArgumentException if the specified queryType is not supported
     *                                  (i.e., not handled in the switch statement)
     */
    public static Query createQuery(QueryType queryType, List<String> arguments, boolean negate) {
        return switch (queryType) {
            case USER_OF -> new UserOfQuery(arguments, negate);
            case ACTIVE_USER_OF -> new ActiveUserOfQuery(arguments, negate);
            case TRANSACTION_SUM_COMPARE -> new TransactionSumCompareQuery(arguments, negate);
            case TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW ->
                    new TransactionSumCompareDepositWithdrawQuery(arguments, negate);
            default -> throw new IllegalArgumentException("Unknown query type: " + queryType);
        };
    }
}