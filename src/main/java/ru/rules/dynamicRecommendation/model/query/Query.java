package ru.rules.dynamicRecommendation.model.query;

import ru.rules.dynamicRecommendation.enums.QueryType;
import ru.rules.dynamicRecommendation.model.Users;
import ru.rules.dynamicRecommendation.repository.TransactionRepository;

import java.util.List;

/**
 * Abstract base class for all query types in the dynamic recommendation system.
 * Defines the common structure and contract for different types of queries
 * used to evaluate user conditions for recommendation rules.
 * <p>
 * Each concrete query implementation must:
 * - Specify the logic for evaluating conditions in the {@link #evaluate} method
 * - Use the provided arguments and query type to determine evaluation criteria
 * - Respect the negation flag to optionally invert the result
 */
public abstract class Query {

    /**
     * Type of the query that determines its evaluation logic and purpose.
     * Defines which condition will be checked (e.g., user activity, transaction sums).
     * Used to route execution to the appropriate evaluation implementation.
     */
    protected QueryType queryType;

    /**
     * List of string arguments containing parameters required for query execution.
     * The structure and meaning of arguments depend on the query type.
     * For example:
     * - For USER_OF: contains product type (e.g., "DEBIT")
     * - For TRANSACTION_SUM_COMPARE: contains product type, transaction type, operator, threshold
     * <p>
     * Must not be null; may be empty for queries that don't require parameters.
     */
    protected List<String> arguments;

    /**
     * Flag indicating whether to negate the result of the query evaluation.
     * If true, the final result will be inverted:
     * - true → false
     * - false → true
     * Enables flexible rule configuration by allowing "NOT" conditions in rules.
     */
    protected boolean negate;

    /**
     * Constructor to initialize a query with its type, arguments, and negation setting.
     *
     * @param queryType the type of query that defines its evaluation logic;
     *                  must not be null
     * @param arguments list of string parameters required for the query execution;
     *                  may be empty but not null
     * @param negate    flag indicating whether to invert the evaluation result;
     *                  false by default (don't negate)
     * @throws IllegalArgumentException if queryType is null
     */
    public Query(QueryType queryType, List<String> arguments, boolean negate) {
        if (queryType == null) {
            throw new IllegalArgumentException("Query type cannot be null");
        }
        if (arguments == null) {
            throw new IllegalArgumentException("Arguments list cannot be null");
        }

        this.queryType = queryType;
        this.arguments = arguments;
        this.negate = negate;
    }

    /**
     * Evaluates the query condition for a specific user using transaction data.
     * The actual evaluation logic is implemented by concrete subclasses.
     *
     * @param user                  the user for whom the recommendation is being evaluated;
     *                              must not be null
     * @param transactionRepository repository for accessing transaction data;
     *                              must not be null
     * @return boolean result of the condition check:
     * - true: condition is met (or not met if negate is true)
     * - false: condition is not met (or met if negate is true when negate is applied)
     * @throws IllegalArgumentException if user or transactionRepository is null
     * @throws RuntimeException         if evaluation fails due to data issues
     */
    public abstract boolean evaluate(Users user, TransactionRepository transactionRepository);
}
