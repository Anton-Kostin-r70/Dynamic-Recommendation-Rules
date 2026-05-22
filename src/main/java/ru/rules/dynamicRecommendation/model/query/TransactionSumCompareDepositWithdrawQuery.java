package ru.rules.dynamicRecommendation.model.query;

import ru.rules.dynamicRecommendation.dto.QueryDTO;
import ru.rules.dynamicRecommendation.enums.ComparisonOperatorType;
import ru.rules.dynamicRecommendation.enums.ProductType;
import ru.rules.dynamicRecommendation.repository.KnowledgeRepository;

import static ru.rules.dynamicRecommendation.enums.QueryType.TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW;

/**
 * Query implementation that compares deposit and withdrawal sums for a specific product type.
 * Evaluates whether the relationship between deposits and withdrawals matches the specified operator.
 * <p>
 * Used in recommendation rules to target users based on their transaction patterns:
 * - Users with high deposits relative to withdrawals
 * - Users with balanced deposit/withdrawal activity
 * - Users with high withdrawals relative to deposits
 */
public class TransactionSumCompareDepositWithdrawQuery extends Query {

    private final KnowledgeRepository knowledgeRepository;

    /**
     * Constructor for creating a {@code TransactionSumCompareDepositWithdrawQuery} instance from a QueryDTO object.
     *
     * @param queryDTO            data transfer object containing query configuration; must not be null.
     *                            The DTO should provide:
     *                            <ul>
     *                            <li>{@code query} — must be "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW" (validated by the parent constructor)</li>
     *                            <li>{@code arguments} — list of string parameters; must contain exactly two elements:
     *                                <ol>
     *                                <li>Product type as a string (e.g., "DEBIT", "CREDIT") — converted to {@link ProductType} enum</li>
     *                                <li>Comparison operator as a string (e.g., ">", "<", "=") — converted to {@link ComparisonOperatorType} enum</li>
     *                                </ol>
     *                            </li>
     *                            <li>{@code negate} — flag indicating whether to negate the evaluation result:
     *                                <ul>
     *                                <li><b>false</b>: return {@code true} if the comparison condition is met (deposit sum vs withdraw sum)</li>
     *                                <li><b>true</b>: return {@code true} if the comparison condition is NOT met</li>
     *                                </ul>
     *                            </li>
     *                            </ul>
     * @param knowledgeRepository repository providing business logic for transaction sum calculations and comparisons
     *                            (deposit vs withdraw sums for a product type); must not be null. This repository
     *                            will be used in the {@link #evaluate} method to perform the actual comparison logic.
     * @throws IllegalArgumentException if:
     *                                  <ul>
     *                                  <li>{@code queryDTO} is null</li>
     *                                  <li>the query type extracted from {@code queryDTO.getQuery()} is not "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW"</li>
     *                                  <li>the arguments list ({@code queryDTO.getArguments()}) is null</li>
     *                                  <li>the arguments list does not contain exactly 2 elements</li>
     *                                  <li>any of the arguments is invalid (cannot be parsed to the corresponding enum)</li>
     *                                  </ul>
     * @throws NullPointerException     if {@code knowledgeRepository} is null
     */
    public TransactionSumCompareDepositWithdrawQuery(QueryDTO queryDTO, KnowledgeRepository knowledgeRepository) {
        super(queryDTO);
        this.knowledgeRepository = knowledgeRepository;
        validateArguments(2, TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW);
    }

    /**
     * Evaluates the condition by comparing deposit and withdrawal transaction sums for a user's product.
     * <p>
     * The evaluation logic:
     * 1. Extracts parameters from arguments:
     *    <ul>
     *    <li>{@code arguments.get(0)} — product type (parsed to {@link ProductType})</li>
     *    <li>{@code arguments.get(1)} — comparison operator (parsed to {@link ComparisonOperatorType})</li>
     *    </ul>
     * 2. Retrieves total deposit and withdrawal amounts for the specified product type.
     * 3. Compares the sums using the specified operator via {@link KnowledgeRepository}.
     * 4. Applies negation if the {@code negate} flag is set.
     *
     * @param userId the ID of the user to evaluate; must not be null
     * @return boolean result of the evaluation:
     * <ul>
     * <li><b>true</b>: condition is met
     *   <ul>
     *   <li>deposit and withdrawal sums meet the comparison criteria when {@code negate = false}</li>
     *   <li>deposit and withdrawal sums do NOT meet the criteria when {@code negate = true}</li>
     *   </ul>
     * </li>
     * <li><b>false</b>: condition is not met
     *   <ul>
     *   <li>deposit and withdrawal sums do NOT meet the criteria when {@code negate = false}</li>
     *   <li>deposit and withdrawal sums meet the criteria when {@code negate = true}</li>
     *   </ul>
     * </li>
     * </ul>
     *
     * @throws IllegalArgumentException if:
     *   <ul>
     *   <li>{@code userId} is null</li>
     *   <li>{@code arguments} list doesn't contain exactly 2 elements</li>
     *   <li>product type argument is invalid (cannot be parsed to a valid {@link ProductType})</li>
     *   <li>operator argument is invalid (cannot be parsed to a valid {@link ComparisonOperatorType}, or is an unsupported operator)</li>
     *   </ul>
     * @throws RuntimeException if database access fails during sum calculations
     *        (e.g., connection issues, query execution errors, or data retrieval problems)
     */
    @Override
    public boolean evaluate(Long userId) {
        ProductType depositProductType = ProductType.fromType(arguments.get(0));
        ComparisonOperatorType operator = ComparisonOperatorType.fromType(arguments.get(1));

        boolean result = knowledgeRepository.compareDepositWithdraw(
                userId, depositProductType, operator
        );
        return negate != result;
    }
}
