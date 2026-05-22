package ru.rules.dynamicRecommendation.model.query;


import ru.rules.dynamicRecommendation.dto.QueryDTO;
import ru.rules.dynamicRecommendation.enums.ComparisonOperatorType;
import ru.rules.dynamicRecommendation.enums.ProductType;
import ru.rules.dynamicRecommendation.enums.TransactionType;
import ru.rules.dynamicRecommendation.repository.KnowledgeRepository;

import static ru.rules.dynamicRecommendation.enums.QueryType.TRANSACTION_SUM_COMPARE;

/**
 * Query implementation that compares a user's transaction sum for a specific product and transaction type
 * against a constant value using a specified comparison operator.
 * Used in recommendation rules to target users based on transaction volume thresholds:
 * - Users with high deposit amounts
 * - Users exceeding minimum transaction thresholds
 * - Users with low withdrawal activity
 */
public class TransactionSumCompareQuery extends Query {

    private final KnowledgeRepository knowledgeRepository;

    /**
     * Constructor for creating a {@code TransactionSumCompareQuery} instance from a QueryDTO object.
     *
     * @param queryDTO            data transfer object containing query configuration; must not be null.
     *                            The DTO should provide:
     *                            <ul>
     *                            <li>{@code query} — must be "TRANSACTION_SUM_COMPARE" (validated by the parent constructor)</li>
     *                            <li>{@code arguments} — list of string parameters; must contain exactly four elements:
     *                                <ol>
     *                                <li>Product type as a string (e.g., "DEBIT", "CREDIT") — converted to {@link ProductType} enum</li>
     *                                <li>Transaction type as a string ("DEPOSIT" or "WITHDRAW") — converted to {@link TransactionType} enum</li>
     *                                <li>Comparison operator as a string (e.g., ">", "<", "=") — converted to {@link ComparisonOperatorType} enum</li>
     *                                <li>Constant value as a string — converted to {@code long}, representing the threshold amount for comparison</li>
     *                                </ol>
     *                            </li>
     *                            <li>{@code negate} — flag indicating whether to negate the evaluation result:
     *                                <ul>
     *                                <li><b>false</b>: return {@code true} if the comparison condition is met</li>
     *                                <li><b>true</b>: return {@code true} if the comparison condition is NOT met</li>
     *                                </ul>
     *                            </li>
     *                            </ul>
     * @param knowledgeRepository repository providing business logic for transaction sum calculations and comparisons;
     *                            must not be null. This repository will be used in the {@link #evaluate} method
     *                            to perform the actual comparison logic.
     * @throws IllegalArgumentException if:
     *                                  <ul>
     *                                  <li>{@code queryDTO} is null</li>
     *                                  <li>the query type extracted from {@code queryDTO.getQuery()} is not "TRANSACTION_SUM_COMPARE"</li>
     *                                  <li>the arguments list ({@code queryDTO.getArguments()}) is null</li>
     *                                  <li>the arguments list does not contain exactly 4 elements</li>
     *                                  <li>any of the arguments is invalid (cannot be parsed to the corresponding enum or long value)</li>
     *                                  </ul>
     * @throws NullPointerException     if {@code knowledgeRepository} is null
     */
    public TransactionSumCompareQuery(QueryDTO queryDTO, KnowledgeRepository knowledgeRepository) {
        super(queryDTO);
        this.knowledgeRepository = knowledgeRepository;
        validateArguments(4, TRANSACTION_SUM_COMPARE);
    }

    /**
     * Evaluates the condition by comparing a user's transaction sum against a constant threshold.
     * <p>
     * The evaluation logic:
     * 1. Extracts parameters from arguments:
     *    <ul>
     *    <li>{@code arguments.get(0)} — product type (parsed to {@link ProductType})</li>
     *    <li>{@code arguments.get(1)} — transaction type (parsed to {@link TransactionType})</li>
     *    <li>{@code arguments.get(2)} — comparison operator (parsed to {@link ComparisonOperatorType})</li>
     *    <li>{@code arguments.get(3)} — threshold constant (as string, parsed to integer)</li>
     *    </ul>
     * 2. Retrieves the total transaction sum for the specified product and transaction types.
     * 3. Compares the sum against the threshold using the specified operator.
     * 4. Applies negation if the {@code negate} flag is set.
     *
     * @param userId the ID of the user to evaluate; must not be null
     * @return boolean result of the evaluation:
     * <ul>
     * <li><b>true</b>: condition is met
     *   <ul>
     *   <li>transaction sum meets the comparison criteria when {@code negate = false}</li>
     *   <li>transaction sum does NOT meet the criteria when {@code negate = true}</li>
     *   </ul>
     * </li>
     * <li><b>false</b>: condition is not met
     *   <ul>
     *   <li>transaction sum does NOT meet the criteria when {@code negate = false}</li>
     *   <li>transaction sum meets the criteria when {@code negate = true}</li>
     *   </ul>
     * </li>
     * </ul>
     *
     * @throws IllegalArgumentException if:
     *   <ul>
     *   <li>{@code userId} is null</li>
     *   <li>{@code arguments} list doesn't contain exactly 4 elements</li>
     *   <li>product type argument is invalid (cannot be parsed to a valid {@link ProductType})</li>
     *   <li>transaction type argument is invalid (cannot be parsed to a valid {@link TransactionType})</li>
     *   <li>operator argument is invalid (cannot be parsed to a valid {@link ComparisonOperatorType}, or is an unsupported operator)</li>
     *   <li>threshold constant is not a valid integer string (e.g., "abc", null, or empty)</li>
     *   </ul>
     * @throws RuntimeException if database access fails during sum calculation
     *        (e.g., connection issues, query execution errors, or data retrieval problems)
     */
    @Override
    public boolean evaluate(Long userId) {
        ProductType productType = ProductType.fromType(arguments.get(0));
        TransactionType transactionType = TransactionType.fromType(arguments.get(1));
        ComparisonOperatorType operator = ComparisonOperatorType.fromType(arguments.get(2));
        int constant = Integer.parseInt(arguments.get(3));

        boolean result = knowledgeRepository.compareTransactionSum(
                userId, productType, transactionType, operator, constant);
        return negate != result;
    }
}