package ru.rules.dynamicRecommendation.model.query;

import ru.rules.dynamicRecommendation.dto.QueryDTO;
import ru.rules.dynamicRecommendation.enums.QueryType;

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
     * Constructor to initialize a query from a QueryDTO object, extracting the query type,
     * arguments, and negation setting.
     *
     * @param queryDTO data transfer object containing query configuration; must not be null.
     *                 The DTO should provide:
     *                 <ul>
     *                 <li>{@code query} — query type identifier that will be converted to {@link QueryType}</li>
     *                 <li>{@code arguments} — list of string parameters for query execution</li>
     *                 <li>{@code negate} — flag indicating whether to invert the evaluation result</li>
     *                 </ul>
     * @throws IllegalArgumentException if:
     *                                  <ul>
     *                                  <li>{@code queryDTO} is null</li>
     *                                  <li>the query type extracted from {@code queryDTO.getQuery()} is invalid or cannot be converted
     *                                      to a {@link QueryType} enum value</li>
     *                                  <li>the arguments list ({@code queryDTO.getArguments()}) is null</li>
     *                                  </ul>
     */
    public Query(QueryDTO queryDTO) {
        if (queryDTO == null) {
            throw new IllegalArgumentException("Query DTO cannot be null");
        }
        QueryType queryType = QueryType.valueOf(queryDTO.getQuery());
        List<String> arguments = queryDTO.getArguments();
        if (arguments == null) {
            throw new IllegalArgumentException("Arguments list cannot be null");
        }

        this.queryType = queryType;
        this.arguments = arguments;
        this.negate = queryDTO.isNegate();
    }

    /**
     * Evaluates the query condition for a specific user using transaction data.
     * The actual evaluation logic is implemented by concrete subclasses.
     *
     * @param userId the ID of the user for whom the condition is being evaluated; must not be null
     * @return boolean result of the condition check:
     * <ul>
     * <li><b>true</b>: condition is met</li>
     * <li><b>false</b>: condition is not met</li>
     * </ul>
     * The final result is affected by the {@code negate} flag (if applicable):
     * <ul>
     * <li>When {@code negate = false}: returns the raw evaluation result</li>
     * <li>When {@code negate = true}: returns the logical negation of the evaluation result</li>
     * </ul>
     *
     * @throws IllegalArgumentException if {@code userId} is null
     * @throws RuntimeException if evaluation fails due to data issues, repository errors,
     *         or internal processing problems
     */
    public abstract boolean evaluate(Long userId);
    /**
     * Validates that the number of provided arguments matches the expected count for the query type.
     * Performed during query initialization to ensure correct configuration before evaluation.
     *
     * @param expectedCount the expected number of arguments required for this query type;
     *                      must be non‑negative
     * @param queryType     the type of query being validated; used for error message context;
     *                      must not be null
     * @throws IllegalArgumentException if:
     *                                  <ul>
     *                                  <li>the actual number of arguments ({@code arguments.size()}) does not match {@code expectedCount}</li>
     *                                  <li>{@code expectedCount} is negative</li>
     *                                  <li>{@code queryType} is null</li>
     *                                  </ul>
     */
    protected void validateArguments(int expectedCount, QueryType queryType) {
        if (queryType == null) {
            throw new IllegalArgumentException("Query type cannot be null");
        }

        if (expectedCount < 0) {
            throw new IllegalArgumentException("Expected argument count cannot be negative");
        }

        int actualCount = arguments.size();
        if (actualCount != expectedCount) {
            throw new IllegalArgumentException(
                    "Invalid number of arguments for " + queryType.getType() +
                            ": expected " + expectedCount + ", but got " + actualCount
            );
        }
    }
}
