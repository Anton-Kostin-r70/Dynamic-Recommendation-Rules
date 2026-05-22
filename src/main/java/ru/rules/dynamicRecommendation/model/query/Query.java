package ru.rules.dynamicRecommendation.model.query;

import ru.rules.dynamicRecommendation.dto.QueryDTO;
import ru.rules.dynamicRecommendation.enums.QueryType;

import java.util.List;

/**
 * Abstract base class for all query types in the dynamic recommendation system.
 * Establishes a common contract and structure for query implementations
 * used to evaluate user conditions within recommendation rules.
 *
 * <p>Concrete query implementations must:</p>
 * <ul>
 *   <li>Implement the evaluation logic in the {@link #evaluate} method</li>
 *   <li>Use the provided arguments and query type to determine evaluation criteria</li>
 *   <li>Respect the negation flag to optionally invert the result</li>
 * </ul>
 */
public abstract class Query {

    /**
     * The query type that determines evaluation logic and purpose.
     * Specifies which condition will be checked (e.g., user activity, transaction sums, etc.).
     * Used to route execution to the appropriate evaluation implementation.
     */
    protected QueryType queryType;

    /**
     * List of string arguments containing parameters required for query execution.
     * The structure and meaning of arguments are query‑type‑specific.
     *
     * <p>Examples:</p>
     * <ul>
     *   <li><b>USER_OF</b>: contains product type (e.g., "DEBIT")</li>
     *   <li><b>TRANSACTION_SUM_COMPARE</b>: contains product type, transaction type, operator, threshold</li>
     * </ul>
     *
     * <p><b>Constraints:</b></p>
     * <ul>
     *   <li>Must not be {@code null}</li>
     *   <li>May be empty for queries that don't require parameters</li>
     * </ul>
     */
    protected List<String> arguments;

    /**
     * Flag indicating whether to negate the result of the query evaluation.
     * When {@code true}, the final result is logically inverted:
     * <ul>
     *   <li>{@code true} → {@code false}</li>
     *   <li>{@code false} → {@code true}</li>
     * </ul>
     *
     * <p>Enables flexible rule configuration by supporting "NOT" conditions in rules.</p>
     */
    protected boolean negate;

    /**
     * Constructs a query instance from a {@link QueryDTO} object, extracting the query type,
     * arguments, and negation setting.
     *
     * @param queryDTO the data transfer object containing query configuration; must not be {@code null}.
     *                 The DTO must provide:
     *                 <ul>
     *                   <li>{@code query} — query type identifier (converted to {@link QueryType})</li>
     *                   <li>{@code arguments} — list of string parameters for query execution</li>
     *                   <li>{@code negate} — flag indicating whether to invert the evaluation result</li>
     *                 </ul>
     * @throws IllegalArgumentException if:
     *                                                  <ul>
     *                                    <li>{@code queryDTO} is {@code null}</li>
     *                                    <li>the query type from {@code queryDTO.getQuery()} is invalid or cannot be converted
     *                                        to a {@link QueryType} enum value</li>
     *                                    <li>the arguments list ({@code queryDTO.getArguments()}) is {@code null}</li>
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
     * @param userId the ID of the user for whom the condition is evaluated; must not be {@code null}
     * @return boolean result of the condition check:
     * <ul>
     *   <li><b>{@code true}</b>: condition is met</li>
     *   <li><b>{@code false}</b>: condition is not met</li>
     * </ul>
     * The final result is affected by the {@code negate} flag:
     * <ul>
     *   <li>When {@code negate = false}: returns the raw evaluation result</li>
     *   <li>When {@code negate = true}: returns the logical negation of the evaluation result</li>
     * </ul>
     * @throws IllegalArgumentException if {@code userId} is {@code null}
     * @throws RuntimeException         if evaluation fails due to:
     *                                  <ul>
     *                                    <li>data issues</li>
     *                                    <li>repository errors</li>
     *                                    <li>internal processing problems</li>
     *                                  </ul>
     */
    public abstract boolean evaluate(Long userId);

    /**
     * Validates that the number of provided arguments matches the expected count for the query type.
     * Performed during query initialization to ensure correct configuration before evaluation.
     *
     * @param expectedCount the expected number of arguments required for this query type;
     *                      must be non‑negative
     * @param queryType     the type of query being validated; used for error message context;
     *                      must not be {@code null}
     * @throws IllegalArgumentException if:
     *                                                  <ul>
     *                                    <li>the actual number of arguments ({@code arguments.size()}) does not match {@code expectedCount}</li>
     *                                    <li>{@code expectedCount} is negative</li>
     *                                    <li>{@code queryType} is {@code null}</li>
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
