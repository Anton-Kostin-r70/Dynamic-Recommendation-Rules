package ru.rules.dynamicRecommendation.model.query;

import ru.rules.dynamicRecommendation.dto.QueryDTO;
import ru.rules.dynamicRecommendation.enums.ComparisonOperatorType;
import ru.rules.dynamicRecommendation.enums.ProductType;
import ru.rules.dynamicRecommendation.repository.KnowledgeRepository;
import ru.rules.dynamicRecommendation.repository.TransactionRepository;

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
     * @param queryDTO data transfer object containing query configuration; must not be null.
     *               The DTO should provide:
     *               <ul>
     *               <li>{@code query} — must be "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW" (validated by the parent constructor)</li>
     *               <li>{@code arguments} — list of string parameters; must contain exactly two elements:
     *                   <ol>
     *                   <li>Product type as a string (e.g., "DEBIT", "CREDIT") — converted to {@link ProductType} enum</li>
     *                   <li>Comparison operator as a string (e.g., ">", "<", "=") — converted to {@link ComparisonOperatorType} enum</li>
     *                   </ol>
     *               </li>
     *               <li>{@code negate} — flag indicating whether to negate the evaluation result:
     *                   <ul>
     *                   <li><b>false</b>: return {@code true} if the comparison condition is met (deposit sum vs withdraw sum)</li>
     *                   <li><b>true</b>: return {@code true} if the comparison condition is NOT met</li>
     *                   </ul>
     *               </li>
     *               </ul>
     * @param knowledgeRepository repository providing business logic for transaction sum calculations and comparisons
     *                        (deposit vs withdraw sums for a product type); must not be null. This repository
     *                        will be used in the {@link #evaluate} method to perform the actual comparison logic.
     * @throws IllegalArgumentException if:
     *        <ul>
     *        <li>{@code queryDTO} is null</li>
     *        <li>the query type extracted from {@code queryDTO.getQuery()} is not "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW"</li>
     *        <li>the arguments list ({@code queryDTO.getArguments()}) is null</li>
     *        <li>the arguments list does not contain exactly 2 elements</li>
     *        <li>any of the arguments is invalid (cannot be parsed to the corresponding enum)</li>
     *        </ul>
     * @throws NullPointerException if {@code knowledgeRepository} is null
     */
    public TransactionSumCompareDepositWithdrawQuery(QueryDTO queryDTO, KnowledgeRepository knowledgeRepository) {
        super(queryDTO);
        this.knowledgeRepository = knowledgeRepository;
        validateArguments(2, TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW);
    }

    /**
     * Evaluates the condition by comparing deposit and withdrawal transaction sums for a userId's product.
     * <p>
     * The evaluation logic:
     * 1. Extracts product type and comparison operator from arguments.
     * 2. Retrieves total deposit and withdrawal amounts for the product type.
     * 3. Compares the sums using the specified operator.
     * 4. Applies negation if the negate flag is set.
     *
     * @param userId                  the userId to evaluate; must not be null
     * @param transactionRepository repository for accessing transaction data; must not be null
     * @return boolean result of the evaluation:
     * - true: condition is met (comparison passes, or fails if negated)
     * - false: condition is not met (comparison fails, or passes if negated)
     * @throws IllegalArgumentException if:
     *                                  - userId is null
     *                                  - transactionRepository is null
     *                                  - arguments list doesn't contain exactly 2 elements
     *                                  - product type or operator argument is invalid
     * @throws RuntimeException         if database access fails during sum calculations
     */
    @Override
    public boolean evaluate(Long userId, TransactionRepository transactionRepository) {
        String depositProductType = arguments.get(0);
        String withdrawProductType = arguments.get(1);

        boolean result = knowledgeRepository.compareDepositWithdraw(
                userId, depositProductType, withdrawProductType
        );
        return negate ? !result : result;
    }
}
