package ru.rules.dynamicRecommendation.model.query;


import ru.rules.dynamicRecommendation.dto.QueryDTO;
import ru.rules.dynamicRecommendation.enums.ComparisonOperatorType;
import ru.rules.dynamicRecommendation.enums.ProductType;
import ru.rules.dynamicRecommendation.enums.TransactionType;
import ru.rules.dynamicRecommendation.model.Users;
import ru.rules.dynamicRecommendation.repository.KnowledgeRepository;
import ru.rules.dynamicRecommendation.repository.TransactionRepository;

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
     * The evaluation logic:
     * 1. Extracts product type, transaction type, operator, and constant from arguments.
     * 2. Retrieves the total transaction sum for the specified product and transaction types.
     * 3. Compares the sum against the constant using the specified operator.
     * 4. Applies negation if the negate flag is set.
     *
     * @param user                  the user to evaluate; must not be null
     * @param transactionRepository repository for accessing transaction data; must not be null
     * @return boolean result of the evaluation:
     * - true: condition is met (comparison passes, or fails if negated)
     * - false: condition is not met (comparison fails, or passes if negated)
     * @throws IllegalArgumentException if:
     *                                  - user is null
     *                                  - transactionRepository is null
     *                                  - arguments list doesn't contain exactly 4 elements
     *                                  - any argument is invalid (unparsable product type, transaction type, operator, or constant)
     */
    @Override
    public boolean evaluate(Users user, TransactionRepository transactionRepository) {
        String productType = arguments.get(0);
        String transactionType = arguments.get(1);
        String operator = arguments.get(2);
        int threshold = Integer.parseInt(arguments.get(3));

        boolean result = knowledgeRepository.compareTransactionSum(
                user.getId(), productType, transactionType, operator, threshold
        );
        return negate ? !result : result;
    }
}